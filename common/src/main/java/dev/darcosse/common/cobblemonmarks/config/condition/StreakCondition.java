package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * Condition based on a win streak (consecutive victories).
 * Unlike standard KillConditions, it usually counts any victory to maintain
 * or increase the current streak stored in NBT.
 *
 * @author Darcosse
 * @version 1.0
 * @since 2026
 */
public class StreakCondition extends KillCondition {

    /**
     * Constructs a StreakCondition with a required target number and a tracking key.
     * * @param requiredStreak The number of consecutive wins needed.
     * @param nbtKey The NBT key used to store the current streak count.
     */
    public StreakCondition(int requiredStreak, String nbtKey) {
        super(requiredStreak, List.of(), List.of(), nbtKey);
    }

    /**
     * Validation logic for the streak.
     * Returns true by default to signify that any victory is eligible to
     * contribute to the streak, as specific reset logic is handled globally.
     */
    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        return true;
    }
}