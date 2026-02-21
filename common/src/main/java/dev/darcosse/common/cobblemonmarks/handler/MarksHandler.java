package dev.darcosse.common.cobblemonmarks.handler;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent;
import com.cobblemon.mod.common.api.events.pokemon.FriendshipUpdatedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent;
import com.cobblemon.mod.common.api.mark.Marks;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.darcosse.common.cobblemonmarks.CobblemonMarksMod;
import dev.darcosse.common.cobblemonmarks.config.Conditions;
import dev.darcosse.common.cobblemonmarks.config.MarksCondition;
import dev.darcosse.common.cobblemonmarks.config.MarksConfig;
import dev.darcosse.common.cobblemonmarks.config.condition.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class MarksHandler {

    private static final String IN_PVW_BATTLE_KEY = "markfarm_in_pvw_battle";

    public static void register() {
        CobblemonEvents.BATTLE_STARTED_POST.subscribe(event -> {
            handleBattleStarted(event.getBattle());
        });

        CobblemonEvents.FRIENDSHIP_UPDATED.subscribe(event -> {
            handleFriendshipUpdated(event);
        });

        CobblemonEvents.BATTLE_VICTORY.subscribe(event -> {
            handleBattleVictory(event.getBattle());
        });

        CobblemonEvents.BATTLE_FAINTED.subscribe(MarksHandler::handleFainted);

        CobblemonEvents.POKEMON_CAPTURED.subscribe(MarksHandler::handleCapture);
    }

    // -------------------------------------------------------------------------

    private static void handleBattleStarted(PokemonBattle battle) {
        if (!battle.isPvW()) return;

        for (BattleActor actor : battle.getActors()) {
            if (!(actor instanceof PlayerBattleActor playerActor)) continue;
            for (BattlePokemon bp : playerActor.getPokemonList()) {
                bp.getOriginalPokemon().getPersistentData().putBoolean(IN_PVW_BATTLE_KEY, true);
            }
        }
    }

    private static void handleBattleVictory(PokemonBattle battle) {
        if (!battle.isPvW()) return;

        PlayerBattleActor playerActor = null;
        for (BattleActor actor : battle.getActors()) {
            if (actor instanceof PlayerBattleActor pba) { playerActor = pba; break; }
        }
        if (playerActor == null) return;

        ServerPlayer player = playerActor.getEntity();

        BattlePokemon activeBp = playerActor.getActivePokemon().getFirst().getBattlePokemon();
        if (activeBp == null) return;
        Pokemon killerPokemon = activeBp.getOriginalPokemon();

        killerPokemon.getPersistentData().putInt(
                TimeOfBattleCondition.TURN_COUNT_KEY,
                battle.getTurn()
        );

        List<Pokemon> defeatedPokemons = new ArrayList<>();
        for (BattleActor actor : battle.getActors()) {
            if (actor instanceof PlayerBattleActor) continue;
            for (BattlePokemon bp : actor.getPokemonList()) {
                defeatedPokemons.add(bp.getOriginalPokemon());
            }
        }

        if (!defeatedPokemons.isEmpty()) {
            for (Pokemon defeated : defeatedPokemons) {
                for (MarksCondition markCondition : MarksConfig.CONDITIONS) {
                    if (shouldSkipBattleVictory(markCondition)) continue;
                    if (hasMark(killerPokemon, markCondition)) continue;
                    evaluateAndProcess(markCondition, killerPokemon, defeated, player);
                }
            }
        }

        // Nettoyer
        killerPokemon.getPersistentData().remove(TimeOfBattleCondition.TURN_COUNT_KEY);
        clearBattleFlag(battle);
    }

    private static void clearBattleFlag(PokemonBattle battle) {
        for (BattleActor actor : battle.getActors()) {
            if (!(actor instanceof PlayerBattleActor)) continue;
            for (BattlePokemon bp : actor.getPokemonList()) {
                bp.getOriginalPokemon().getPersistentData().remove(IN_PVW_BATTLE_KEY);
            }
        }
    }

    private static void handleFainted(BattleFaintedEvent event) {
        if (!event.getBattle().isPvW()) return;
        if (!(event.getKilled().getActor() instanceof PlayerBattleActor playerActor)) return;

        Pokemon faintedPokemon = event.getKilled().getOriginalPokemon();
        ServerPlayer player = playerActor.getEntity();

        for (MarksCondition markCondition : MarksConfig.CONDITIONS) {
            if (hasMark(faintedPokemon, markCondition)) continue;

            // Reset streak si dans le slot killCondition
            KillCondition killCond = markCondition.getConditions().getKillCondition();
            if (killCond instanceof StreakCondition) {
                int current = faintedPokemon.getPersistentData().getInt(killCond.getNbtKey());
                if (current > 0) {
                    faintedPokemon.getPersistentData().remove(killCond.getNbtKey());
                    player.sendSystemMessage(
                            Component.literal("§c[" + faintedPokemon.getSpecies().getName()
                                    + "] Streak perdu ! (0/" + killCond.getRequiredCount() + ")"),
                            true
                    );
                }
            }

            // Death conditions dans required
            for (MarkCondition condition : markCondition.getConditions().getRequired()) {
                if (condition instanceof DeathCondition) {
                    processCounter(faintedPokemon, condition, markCondition, null, player);
                }
            }
        }
    }

    private static void handleCapture(PokemonCapturedEvent event) {
        if (!event.getPokemon().getShiny()) return;
        ServerPlayer player = event.getPlayer();

        // Chercher le pokémon actif en combat PvW via le flag
        var party = Cobblemon.INSTANCE.getStorage().getParty(player);
        Pokemon activePokemon = null;
        for (Pokemon p : party) {
            if (p != null && p.getPersistentData().getBoolean(IN_PVW_BATTLE_KEY)) {
                activePokemon = p;
                break;
            }
        }
        if (activePokemon == null) return;

        for (MarksCondition markCondition : MarksConfig.CONDITIONS) {
            if (hasMark(activePokemon, markCondition)) continue;

            KillCondition killCond = markCondition.getConditions().getKillCondition();
            if (killCond == null) continue;

            boolean hasShinyCondition = false;
            for (MarkCondition c : markCondition.getConditions().getRequired()) {
                if (c instanceof ShinyCondition) { hasShinyCondition = true; break; }
            }
            if (!hasShinyCondition) continue;

            processCounter(activePokemon, killCond, markCondition, null, player);
        }
    }

    private static void handleFriendshipUpdated(FriendshipUpdatedEvent event) {
        Pokemon pokemon = event.getPokemon();
        if (pokemon == null) return;

        // Récupérer le joueur propriétaire du pokémon
        var storage = Cobblemon.INSTANCE.getStorage();
        ServerPlayer player = null;
        // À adapter selon l'API disponible sur l'event
        if (event.getPokemon().getOwnerEntity() instanceof ServerPlayer sp) {
            player = sp;
        }
        if (player == null) return;

        for (MarksCondition markCondition : MarksConfig.CONDITIONS) {
            if (hasMark(pokemon, markCondition)) continue;

            // Vérifier si la mark a une FriendshipCondition dans required sans KillCondition
            Conditions conditions = markCondition.getConditions();
            if (conditions.getKillCondition() != null) continue;

            boolean hasFriendship = false;
            for (MarkCondition c : conditions.getRequired()) {
                if (c instanceof FriendshipCondition fc) {
                    if (pokemon.getFriendship() >= fc.getRequiredFriendship()) {
                        hasFriendship = true;
                    }
                    break;
                }
            }
            if (!hasFriendship) continue;

            awardMark(pokemon, markCondition, player);
        }
    }

    // -------------------------------------------------------------------------

    private static void evaluateAndProcess(MarksCondition markCondition, Pokemon killer,
                                           Pokemon defeated, ServerPlayer player) {
        Conditions conditions = markCondition.getConditions();
        KillCondition killCond = conditions.getKillCondition();

        // Vérifier les conditions requises
        for (MarkCondition c : conditions.getRequired()) {
            if (!c.isMet(killer, defeated, player)) return;
        }

        // Vérifier les conditions exclues — si l'une est vraie on abandonne
        for (MarkCondition c : conditions.getExcluded()) {
            if (c.isMet(killer, defeated, player)) return;
        }

        // Vérifier le filtre type/species/fishing de la KillCondition
        if (killCond != null && !killCond.isMet(killer, defeated, player)) return;

        if (killCond != null) {
            processCounter(killer, killCond, markCondition, defeated, player);
        } else {
            // Mode instantané : WeatherCondition, BiomeCondition, FriendshipCondition...
            awardMark(killer, markCondition, player);
        }
    }

    private static void processCounter(Pokemon pokemon, MarkCondition condition,
                                       MarksCondition markCondition, Pokemon defeated,
                                       ServerPlayer player) {
        var tag = pokemon.getPersistentData();
        int currentCount = tag.getInt(condition.getNbtKey()) + 1;
        tag.putInt(condition.getNbtKey(), currentCount);
        int required = condition.getRequiredCount();

        if (currentCount % 10 == 0 || currentCount == required) {
            player.sendSystemMessage(
                    Component.literal("§6[" + pokemon.getSpecies().getName() + "] §e"
                            + currentCount + "/" + required),
                    true
            );
        }

        if (currentCount >= required) {
            tag.remove(condition.getNbtKey());
            awardMark(pokemon, markCondition, player);
        }
    }

    private static void awardMark(Pokemon pokemon, MarksCondition markCondition,
                                  ServerPlayer player) {
        String rawPath = markCondition.getMarkIdentifier().replace("cobblemon:", "");
        ResourceLocation markId = ResourceLocation.fromNamespaceAndPath("cobblemon", rawPath);
        var mark = Marks.getByIdentifier(markId);

        if (mark == null) {
            CobblemonMarksMod.LOGGER.warn("Mark introuvable : {}", markCondition.getMarkIdentifier());
            return;
        }
        if (pokemon.getMarks().contains(mark)) return;

        pokemon.exchangeMark(mark, true);

        String name = pokemon.getNickname() != null
                ? pokemon.getNickname().getString()
                : pokemon.getSpecies().getName();

        player.sendSystemMessage(
                Component.literal("§a✨ " + name
                        + " a obtenu une nouvelle marque §a!")
        );
    }

    // -------------------------------------------------------------------------

    private static boolean hasMark(Pokemon pokemon, MarksCondition markCondition) {
        String rawPath = markCondition.getMarkIdentifier().replace("cobblemon:", "");
        ResourceLocation markId = ResourceLocation.fromNamespaceAndPath("cobblemon", rawPath);
        var mark = Marks.getByIdentifier(markId);
        return mark != null && pokemon.getMarks().contains(mark);
    }

    private static boolean shouldSkipBattleVictory(MarksCondition markCondition) {
        Conditions conditions = markCondition.getConditions();

        // ShinyCondition dans required → géré par handleCapture
        for (MarkCondition c : conditions.getRequired()) {
            if (c instanceof ShinyCondition) return true;
        }

        // Pas de KillCondition → vérifier si que des Death
        if (conditions.getKillCondition() != null) return false;
        for (MarkCondition c : conditions.getRequired()) {
            if (!(c instanceof DeathCondition)) return false;
        }
        return true;
    }
}