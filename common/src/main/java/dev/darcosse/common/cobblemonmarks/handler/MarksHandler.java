package dev.darcosse.common.cobblemonmarks.handler;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent;
import com.cobblemon.mod.common.api.events.pokemon.FriendshipUpdatedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonRecallEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonSentEvent;
import com.cobblemon.mod.common.api.mark.Marks;
import com.cobblemon.mod.common.battles.BattleRegistry;
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

/**
 * Main handler for the Marks system.
 * This class orchestrates event subscriptions, NBT-based progress tracking,
 * and the final awarding of Marks to Pokémon.
 *
 * @author Darcosse
 * @version 1.2
 * @since 2026
 */
public class MarksHandler {

    /** Temporary NBT key used to identify player Pokémon active in wild battles. */
    private static final String IN_PVW_BATTLE_KEY = "markfarm_in_pvw_battle";

    /**
     * Registers all necessary Cobblemon event subscribers.
     */
    public static void register() {
        // Battle lifecycle events
        CobblemonEvents.BATTLE_STARTED_POST.subscribe(event -> handleBattleStarted(event.getBattle()));
        CobblemonEvents.BATTLE_VICTORY.subscribe(event -> handleBattleVictory(event.getBattle()));
        CobblemonEvents.BATTLE_FAINTED.subscribe(MarksHandler::handleFainted);

        CobblemonEvents.POKEMON_SENT_POST.subscribe(MarksHandler::handleSentOut);
        CobblemonEvents.POKEMON_RECALL_POST.subscribe(MarksHandler::handleRecall);

        // Pokémon state events
        CobblemonEvents.POKEMON_CAPTURED.subscribe(MarksHandler::handleCapture);
        CobblemonEvents.FRIENDSHIP_UPDATED.subscribe(MarksHandler::handleFriendshipUpdated);
    }

    /**
     * Updates the active Pokémon flag when a Pokémon is sent out during a PvW battle.
     * Transfers IN_PVW_BATTLE_KEY to the newly active Pokémon.
     */
    private static void handleSentOut(PokemonSentEvent.Post event) {
        Pokemon pokemon = event.getPokemon();
        if (!(pokemon.getOwnerEntity() instanceof ServerPlayer player)) return;

        PokemonBattle battle = BattleRegistry.getBattleByParticipatingPlayer(player);
        if (battle == null || !battle.isPvW()) return;

        var party = Cobblemon.INSTANCE.getStorage().getParty(player);
        for (Pokemon p : party) {
            if (p != null) p.getPersistentData().remove(IN_PVW_BATTLE_KEY);
        }

        pokemon.getPersistentData().putBoolean(IN_PVW_BATTLE_KEY, true);
    }

    /**
     * Removes the active Pokémon flag when a Pokémon is recalled during a PvW battle.
     */
    private static void handleRecall(PokemonRecallEvent.Post event) {
        event.getPokemon().getPersistentData().remove(IN_PVW_BATTLE_KEY);
    }

    /**
     * Flags player Pokémon when entering a PvW (Player vs Wild) battle.
     * This flag ensures the correct Pokémon receives credit during capture events.
     */
    private static void handleBattleStarted(PokemonBattle battle) {
        if (!battle.isPvW()) return;

        for (BattleActor actor : battle.getActors()) {
            if (!(actor instanceof PlayerBattleActor playerActor)) continue;
            playerActor.getPokemonList().getFirst().getOriginalPokemon()
                    .getPersistentData().putBoolean(IN_PVW_BATTLE_KEY, true);
        }
    }

    /**
     * Processes victory rewards. Evaluates turn counts and opponent-based conditions.
     */
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

        // Inject turn count for TimeOfBattleCondition
        killerPokemon.getPersistentData().putInt(TimeOfBattleCondition.TURN_COUNT_KEY, battle.getTurn());

        List<Pokemon> defeatedPokemons = new ArrayList<>();
        for (BattleActor actor : battle.getActors()) {
            if (actor instanceof PlayerBattleActor) continue;
            for (BattlePokemon bp : actor.getPokemonList()) {
                defeatedPokemons.add(bp.getOriginalPokemon());
            }
        }

        if (!defeatedPokemons.isEmpty()) {
            for (Pokemon defeated : defeatedPokemons) {
                if (defeated.getOwnerUUID() != null) continue; // Only count wild targets

                for (MarksCondition markCondition : MarksConfig.CONDITIONS) {
                    if (shouldSkipBattleVictory(markCondition)) continue;
                    if (hasMark(killerPokemon, markCondition)) continue;
                    evaluateAndProcess(markCondition, killerPokemon, defeated, player);
                }
            }
        }

        // Cleanup temporary data
        killerPokemon.getPersistentData().remove(TimeOfBattleCondition.TURN_COUNT_KEY);
        clearBattleFlag(battle);
    }

    /**
     * Removes the battle flag from player Pokémon after the battle ends.
     */
    private static void clearBattleFlag(PokemonBattle battle) {
        for (BattleActor actor : battle.getActors()) {
            if (!(actor instanceof PlayerBattleActor)) continue;
            for (BattlePokemon bp : actor.getPokemonList()) {
                bp.getOriginalPokemon().getPersistentData().remove(IN_PVW_BATTLE_KEY);
            }
        }
    }

    /**
     * Handles fainting events. Manages DeathConditions and resets Win Streaks.
     */
    private static void handleFainted(BattleFaintedEvent event) {
        if (!event.getBattle().isPvW()) return;
        if (!(event.getKilled().getActor() instanceof PlayerBattleActor playerActor)) return;

        Pokemon faintedPokemon = event.getKilled().getOriginalPokemon();
        ServerPlayer player = playerActor.getEntity();

        for (MarksCondition markCondition : MarksConfig.CONDITIONS) {
            if (hasMark(faintedPokemon, markCondition)) continue;

            MarkCondition mainCond = markCondition.getConditions().getKillCondition();

            // Reset streak if the Pokémon faints
            if (mainCond instanceof StreakCondition sc) {
                int currentStreak = faintedPokemon.getPersistentData().getInt(sc.getNbtKey());
                if (currentStreak > 0) {
                    faintedPokemon.getPersistentData().remove(sc.getNbtKey());
                    player.sendSystemMessage(Component.translatable("cobblemonmarks.message.streak_lost",
                            pokemonName(faintedPokemon),
                            Component.literal(String.valueOf(currentStreak)).withStyle(s -> s.withColor(0xFFFFFF)),
                            Component.literal(String.valueOf(sc.getRequiredCount())).withStyle(s -> s.withColor(0xFFFFFF))
                    ).withStyle(s -> s.withColor(0xFF5555)));
                }
            }

            // Progress DeathConditions
            for (MarkCondition condition : markCondition.getConditions().getRequired()) {
                if (condition instanceof DeathCondition) {
                    processCounter(faintedPokemon, condition, markCondition, null, player);
                }
            }
        }
    }

    /**
     * Handles Mark progression triggered by a successful capture.
     */
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

            // Shiny Filter
            boolean hasShinyReq = markCondition.getConditions().getRequired().stream().anyMatch(c -> c instanceof ShinyCondition);
            if (hasShinyReq && !event.getPokemon().getShiny()) continue;

            // Validate requirements and exclusions
            boolean conditionsMet = true;
            for (MarkCondition c : markCondition.getConditions().getRequired()) {
                if (c instanceof ShinyCondition) continue;
                if (!c.isMet(activePokemon, event.getPokemon(), player)) { conditionsMet = false; break; }
            }
            if (!conditionsMet) continue;

            for (MarkCondition c : markCondition.getConditions().getExcluded()) {
                if (c.isMet(activePokemon, event.getPokemon(), player)) { conditionsMet = false; break; }
            }
            if (!conditionsMet) continue;

            processCounter(activePokemon, killCond, markCondition, null, player);
        }
    }

    /**
     * Handles progression based on friendship updates (e.g., Partner Mark).
     */
    private static void handleFriendshipUpdated(FriendshipUpdatedEvent event) {
        Pokemon pokemon = event.getPokemon();
        if (pokemon == null || !(pokemon.getOwnerEntity() instanceof ServerPlayer player)) return;

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

    /**
     * Evaluates if a victory scenario meets all conditions for a specific Mark.
     */
    private static void evaluateAndProcess(MarksCondition markCondition, Pokemon killer, Pokemon defeated, ServerPlayer player) {
        Conditions conditions = markCondition.getConditions();
        MarkCondition mainCond = conditions.getKillCondition();

        for (MarkCondition c : conditions.getRequired()) {
            if (!c.isMet(killer, defeated, player)) return;
        }

        for (MarkCondition c : conditions.getExcluded()) {
            if (c.isMet(killer, defeated, player)) return;
        }

        if (mainCond != null && !mainCond.isMet(killer, defeated, player)) return;

        if (mainCond != null) {
            processCounter(killer, mainCond, markCondition, defeated, player);
        } else {
            awardMark(killer, markCondition, player);
        }
    }

    /**
     * Updates NBT counters and sends progress sync packets to the player.
     */
    private static void processCounter(Pokemon pokemon, MarkCondition condition, MarksCondition markCondition, Pokemon defeated, ServerPlayer player) {
        var tag = pokemon.getPersistentData();
        int currentCount = tag.getInt(condition.getNbtKey()) + 1;
        tag.putInt(condition.getNbtKey(), currentCount);
        int required = condition.getRequiredCount();

        // Network synchronization for UI
        Map<String, Integer> progressMap = new HashMap<>();
        for (MarksCondition mc : MarksConfig.CONDITIONS) {
            MarkCondition kc = mc.getConditions().getKillCondition();
            if (kc != null && kc.getNbtKey() != null) {
                int val = tag.getInt(kc.getNbtKey());
                if (val > 0) progressMap.put(kc.getNbtKey(), val);
            }
            for (MarkCondition c : mc.getConditions().getRequired()) {
                if (c.getNbtKey() != null) {
                    int val = tag.getInt(c.getNbtKey());
                    if (val > 0) progressMap.put(c.getNbtKey(), val);
                }
            }
        }
        PacketSender.sendToPlayer(player, new SyncMarkProgressPayload(pokemon.getUuid(), progressMap));

        if (currentCount >= required) {
            tag.remove(condition.getNbtKey());
            awardMark(pokemon, markCondition, player);
        }
    }

    /**
     * Final step: awards the Mark to the Pokémon and notifies the player.
     */
    private static void awardMark(Pokemon pokemon, MarksCondition markCondition, ServerPlayer player) {
        try {
            String rawPath = markCondition.getMarkIdentifier().replace("cobblemon:", "");
            ResourceLocation markId = ResourceLocation.fromNamespaceAndPath("cobblemon", rawPath);
            var mark = Marks.getByIdentifier(markId);

            if (mark == null) {
                CobblemonMarksMod.LOGGER.warn("Mark not found: {}", markCondition.getMarkIdentifier());
                return;
            }
            if (pokemon.getMarks().contains(mark)) return;

            pokemon.exchangeMark(mark, true);

            String markName = mark.getSerializedName().replace("cobblemon:", "");
            player.sendSystemMessage(Component.translatable("cobblemonmarks.message.mark_obtained",
                    pokemonName(pokemon),
                    Component.translatable("cobblemon.mark." + markName).withStyle(s -> s.withColor(0x55FF55))
            ).withStyle(s -> s.withColor(0x55FF55)));
        } catch (Exception e) {
            CobblemonMarksMod.LOGGER.warn("Invalid markIdentifier '{}': {}", markCondition.getMarkIdentifier(), e.getMessage());
        }
    }

    /**
     * Returns a stylized component for the Pokémon's name or nickname.
     */
    private static MutableComponent pokemonName(Pokemon pokemon) {
        if (pokemon.getNickname() != null) {
            return Component.literal(pokemon.getNickname().getString()).withStyle(s -> s.withColor(0xFFAA00));
        }
        return Component.translatable("cobblemon.species." + pokemon.getSpecies().getName().toLowerCase() + ".name")
                .withStyle(s -> s.withColor(0xFFAA00));
    }

    /**
     * Checks if a Pokémon already possesses a specific Mark.
     */
    private static boolean hasMark(Pokemon pokemon, MarksCondition markCondition) {
        String rawPath = markCondition.getMarkIdentifier().replace("cobblemon:", "");
        ResourceLocation markId = ResourceLocation.fromNamespaceAndPath("cobblemon", rawPath);
        var mark = Marks.getByIdentifier(markId);
        return mark != null && pokemon.getMarks().contains(mark);
    }

    /**
     * Internal filter to determine if a condition should be checked during victory events.
     */
    private static boolean shouldSkipBattleVictory(MarksCondition markCondition) {
        Conditions conditions = markCondition.getConditions();
        if (conditions.getKillCondition() instanceof CatchCondition) return true;
        if (conditions.getKillCondition() != null) return false;
        for (MarkCondition c : conditions.getRequired()) {
            if (!(c instanceof DeathCondition)) return false;
        }
        return true;
    }

    /**
     * Synchronizes Mark progress NBT data to the client on player join.
     * Should be called from the platform-specific login event.
     */
    public static void syncProgressOnJoin(ServerPlayer player) {
        var party = Cobblemon.INSTANCE.getStorage().getParty(player);

        for (Pokemon pokemon : party) {
            if (pokemon == null) continue;

            Map<String, Integer> progressMap = new HashMap<>();
            var tag = pokemon.getPersistentData();

            for (MarksCondition mc : MarksConfig.CONDITIONS) {
                MarkCondition kc = mc.getConditions().getKillCondition();
                if (kc != null && kc.getNbtKey() != null) {
                    int val = tag.getInt(kc.getNbtKey());
                    if (val > 0) progressMap.put(kc.getNbtKey(), val);
                }
                for (MarkCondition c : mc.getConditions().getRequired()) {
                    if (c.getNbtKey() != null) {
                        int val = tag.getInt(c.getNbtKey());
                        if (val > 0) progressMap.put(c.getNbtKey(), val);
                    }
                }
            }

            if (!progressMap.isEmpty()) {
                PacketSender.sendToPlayer(player, new SyncMarkProgressPayload(pokemon.getUuid(), progressMap));
            }
        }
    }
}