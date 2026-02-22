package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

/**
 * Condition based on the duration of the battle (turn count).
 * This can be used to award Marks for quick victories (Blitz)
 * or long, drawn-out tactical battles (Endurance).
 *
 * @author Darcosse
 * @version 1.0
 * @since 2026
 */
public class TimeOfBattleCondition implements MarkCondition {

    /** Key used to retrieve the turn count from the Pokémon's persistent data. */
    public static final String TURN_COUNT_KEY = "markfarm_battle_turns";

    private final Integer minTurns;
    private final Integer maxTurns;

    /**
     * Constructs a TimeOfBattleCondition with specific turn boundaries.
     * * @param minTurns Minimum turns required (inclusive). Null for no minimum.
     * @param maxTurns Maximum turns allowed (inclusive). Null for no maximum.
     */
    public TimeOfBattleCondition(Integer minTurns, Integer maxTurns) {
        this.minTurns = minTurns;
        this.maxTurns = maxTurns;
    }

    /**
     * Validates the condition by reading the turn count injected into the
     * trigger Pokémon's persistent data by the event handler.
     */
    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        // The turn count is temporarily stored in persistentData during the event handling phase.
        int turns = triggerPokemon.getPersistentData().getInt(TURN_COUNT_KEY);

        if (minTurns != null && turns < minTurns) return false;
        if (maxTurns != null && turns > maxTurns) return false;

        return true;
    }

    /**
     * Returns null as this condition checks a temporary state and doesn't
     * need a dedicated progress counter.
     */
    @Override
    public String getNbtKey() { return null; }

    /**
     * Battle duration conditions are binary requirements satisfied once the threshold is reached.
     */
    @Override
    public int getRequiredCount() { return 1; }

    public Integer getMinTurns() { return minTurns; }
    public Integer getMaxTurns() { return maxTurns; }
}