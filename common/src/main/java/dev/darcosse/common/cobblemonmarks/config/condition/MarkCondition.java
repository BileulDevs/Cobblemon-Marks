package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

/**
 * Core interface for all Mark requirements.
 * Defines the contract for validating world states, Pokémon properties,
 * or tracking persistent progress via NBT.
 *
 * @author Darcosse
 * @version 1.0
 * @since 2026
 */
public interface MarkCondition {

    /**
     * Checks if the specific condition is met at the moment of the event.
     * * @param triggerPokemon The player's Pokémon involved (killer or fainted Pokémon).
     * @param targetPokemon  The wild/target Pokémon involved (can be null for some events).
     * @param player         The player performing the action.
     * @return true if the condition is satisfied.
     */
    boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player);

    /**
     * Returns the unique NBT key used to persist the counter for this condition.
     * * @return The NBT key string, or null if the condition is instantaneous (e.g., Weather, Biome).
     */
    String getNbtKey();

    /**
     * Returns the required count/threshold to validate this specific condition.
     * * @return The target integer (returns 1 for binary/instantaneous conditions).
     */
    int getRequiredCount();
}