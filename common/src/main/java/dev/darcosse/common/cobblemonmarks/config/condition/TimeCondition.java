package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

/**
 * Condition based on the world's daytime cycle.
 * It allows for time-specific Marks (e.g., Early Bird or Night Owl insignias)
 * by checking the current world time in ticks.
 *
 * @author Darcosse
 * @version 1.0
 * @since 2026
 */
public class TimeCondition implements MarkCondition {

    private final long minTime;
    private final long maxTime;

    /**
     * Constructs a TimeCondition with a specific tick range (0-23999).
     * * @param minTime The starting tick of the window.
     * @param maxTime The ending tick of the window.
     */
    public TimeCondition(long minTime, long maxTime) {
        this.minTime = minTime;
        this.maxTime = maxTime;
    }

    /**
     * Checks if the current world time falls within the required window.
     * Handles linear ranges (e.g., 6000-12000) and wrapping ranges
     * that cross midnight (e.g., 23000-1000).
     */
    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        long time = player.serverLevel().getDayTime() % 24000;

        if (minTime <= maxTime) {
            // Standard case: range is within a single day cycle
            return time >= minTime && time <= maxTime;
        } else {
            // Wrapping case: range crosses the 24000/0 threshold (Midnight)
            return time >= minTime || time <= maxTime;
        }
    }

    /**
     * Returns null as time checks are environment-based and do not require NBT counters.
     */
    @Override
    public String getNbtKey() { return null; }

    /**
     * Time conditions are validated as a single state requirement.
     */
    @Override
    public int getRequiredCount() { return 1; }

    /**
     * Gets the minimum tick value for description and serialization.
     */
    public long getMinTime() { return minTime; }

    /**
     * Gets the maximum tick value for description and serialization.
     */
    public long getMaxTime() { return maxTime; }
}