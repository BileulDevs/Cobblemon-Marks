package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

/**
 * Condition checking if the Pokémon is shiny.
 * In the current implementation, this serves as a flag for specific Mark requirements,
 * often combined with CatchConditions or KillConditions.
 *
 * @author Darcosse
 * @version 1.0
 * @since 2026
 */
public class ShinyCondition implements MarkCondition {

    /**
     * Validates the shiny requirement.
     * Returns true by default as the actual shiny check is typically performed
     * by the event handler before evaluating the condition list.
     */
    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        return true;
    }

    /**
     * Returns null as the shiny status is a native Pokémon property
     * and does not require an external NBT counter.
     */
    @Override
    public String getNbtKey() { return null; }

    /**
     * Shiny conditions are state-based and satisfied instantly.
     */
    @Override
    public int getRequiredCount() { return 1; }
}