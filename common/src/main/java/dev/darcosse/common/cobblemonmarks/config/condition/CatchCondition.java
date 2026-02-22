package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * Condition triggered specifically when a Pokémon is caught rather than defeated.
 * Inherits from KillCondition to reuse progress tracking and count requirements,
 * but overrides the check logic for capture events.
 *
 * @author Darcosse
 * @version 1.0
 * @since 2026
 */
public class CatchCondition extends KillCondition {

    /**
     * Constructs a CatchCondition with a specific requirement count and NBT storage key.
     * * @param requiredCatches Number of catches needed to satisfy this condition.
     * @param nbtKey The NBT key used to persist progress on the Pokémon.
     */
    public CatchCondition(int requiredCatches, String nbtKey) {
        super(requiredCatches, List.of(), List.of(), nbtKey);
    }

    /**
     * Validation logic for the capture.
     * Returns true by default as specialized filtering (like Shiny status)
     * is handled by separate conditions in the 'required' list.
     */
    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        return true;
    }
}