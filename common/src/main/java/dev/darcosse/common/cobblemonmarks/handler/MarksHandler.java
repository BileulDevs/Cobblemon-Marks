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
import dev.darcosse.common.cobblemonmarks.network.PacketSender;
import dev.darcosse.common.cobblemonmarks.network.SyncMarkProgressPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarksHandler {

    private static final String IN_PVW_BATTLE_KEY = "markfarm_in_pvw_battle";

    public static void register() {
        CobblemonEvents.BATTLE_STARTED_POST.subscribe(event -> {
            handleBattleStarted(event.getBattle());
        });

        CobblemonEvents.FRIENDSHIP_UPDATED.subscribe(MarksHandler::handleFriendshipUpdated);

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
                if (defeated.getOwnerUUID() != null) continue;

                for (MarksCondition markCondition : MarksConfig.CONDITIONS) {
                    if (shouldSkipBattleVictory(markCondition)) continue;
                    if (hasMark(killerPokemon, markCondition)) continue;
                    evaluateAndProcess(markCondition, killerPokemon, defeated, player);
                }
            }
        }

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

            MarkCondition killCond = markCondition.getConditions().getKillCondition();
            if (killCond instanceof StreakCondition sc) {
                int current = faintedPokemon.getPersistentData().getInt(sc.getNbtKey());
                if (current > 0) {
                    faintedPokemon.getPersistentData().remove(sc.getNbtKey());
                    player.sendSystemMessage(
                            Component.translatable("cobblemonmarks.message.streak_lost",
                                    pokemonName(faintedPokemon),
                                    Component.literal("0").withStyle(s -> s.withColor(0xFFFFFF)),
                                    Component.literal(String.valueOf(sc.getRequiredCount())).withStyle(s -> s.withColor(0xFFFFFF))
                            ).withStyle(s -> s.withColor(0xFF5555))
                    );
                }
            }

            for (MarkCondition condition : markCondition.getConditions().getRequired()) {
                if (condition instanceof DeathCondition) {
                    processCounter(faintedPokemon, condition, markCondition, null, player);
                }
            }
        }
    }

    private static void handleCapture(PokemonCapturedEvent event) {
        ServerPlayer player = event.getPlayer();

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

            MarkCondition killCond = markCondition.getConditions().getKillCondition();
            if (!(killCond instanceof CatchCondition)) continue;

            boolean hasShiny = markCondition.getConditions().getRequired()
                    .stream().anyMatch(c -> c instanceof ShinyCondition);

            if (hasShiny && !event.getPokemon().getShiny()) continue;

            boolean conditionsMet = true;
            for (MarkCondition c : markCondition.getConditions().getRequired()) {
                if (c instanceof ShinyCondition) continue;
                if (!c.isMet(activePokemon, event.getPokemon(), player)) {
                    conditionsMet = false;
                    break;
                }
            }
            if (!conditionsMet) continue;

            for (MarkCondition c : markCondition.getConditions().getExcluded()) {
                if (c.isMet(activePokemon, event.getPokemon(), player)) {
                    conditionsMet = false;
                    break;
                }
            }
            if (!conditionsMet) continue;

            processCounter(activePokemon, killCond, markCondition, null, player);
        }
    }

    private static void handleFriendshipUpdated(FriendshipUpdatedEvent event) {
        Pokemon pokemon = event.getPokemon();
        if (pokemon == null) return;

        ServerPlayer player = null;
        if (event.getPokemon().getOwnerEntity() instanceof ServerPlayer sp) {
            player = sp;
        }
        if (player == null) return;

        for (MarksCondition markCondition : MarksConfig.CONDITIONS) {
            if (hasMark(pokemon, markCondition)) continue;

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
        MarkCondition killCond = conditions.getKillCondition();

        for (MarkCondition c : conditions.getRequired()) {
            if (!c.isMet(killer, defeated, player)) return;
        }

        for (MarkCondition c : conditions.getExcluded()) {
            if (c.isMet(killer, defeated, player)) return;
        }

        if (killCond != null && !killCond.isMet(killer, defeated, player)) return;

        if (killCond != null) {
            processCounter(killer, killCond, markCondition, defeated, player);
        } else {
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

        // Construire la map complète de progression du pokémon
        Map<String, Integer> progressMap = new HashMap<>();
        for (MarksCondition mc : MarksConfig.CONDITIONS) {
            KillCondition kc = (KillCondition) mc.getConditions().getKillCondition();
            if (kc != null) {
                int val = tag.getInt(kc.getNbtKey());
                if (val > 0) progressMap.put(kc.getNbtKey(), val);
            }
            for (MarkCondition c : mc.getConditions().getRequired()) {
                if (c instanceof DeathCondition dc) {
                    int val = tag.getInt(dc.getNbtKey());
                    if (val > 0) progressMap.put(dc.getNbtKey(), val);
                }
            }
        }
        PacketSender.sendToPlayer(player, new SyncMarkProgressPayload(pokemon.getUuid(), progressMap));

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

        String markName = mark.getSerializedName().replace("cobblemon:", "");
        player.sendSystemMessage(
                Component.translatable("cobblemonmarks.message.mark_obtained",
                        pokemonName(pokemon),
                        Component.translatable("cobblemon.mark." + markName).withStyle(s -> s.withColor(0x55FF55))
                ).withStyle(s -> s.withColor(0x55FF55))
        );
    }

    // -------------------------------------------------------------------------

    private static MutableComponent pokemonName(Pokemon pokemon) {
        if (pokemon.getNickname() != null) {
            return Component.literal(pokemon.getNickname().getString())
                    .withStyle(s -> s.withColor(0xFFAA00));
        }
        return Component.translatable("cobblemon.species." + pokemon.getSpecies().getName().toLowerCase() + ".name")
                .withStyle(s -> s.withColor(0xFFAA00));
    }

    private static boolean hasMark(Pokemon pokemon, MarksCondition markCondition) {
        String rawPath = markCondition.getMarkIdentifier().replace("cobblemon:", "");
        ResourceLocation markId = ResourceLocation.fromNamespaceAndPath("cobblemon", rawPath);
        var mark = Marks.getByIdentifier(markId);
        return mark != null && pokemon.getMarks().contains(mark);
    }

    private static boolean shouldSkipBattleVictory(MarksCondition markCondition) {
        Conditions conditions = markCondition.getConditions();

        if (conditions.getKillCondition() instanceof CatchCondition) return true;

        if (conditions.getKillCondition() != null) return false;
        for (MarkCondition c : conditions.getRequired()) {
            if (!(c instanceof DeathCondition)) return false;
        }
        return true;
    }
}