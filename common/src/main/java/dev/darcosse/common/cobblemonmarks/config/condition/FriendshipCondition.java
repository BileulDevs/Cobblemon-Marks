package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

/**
 * Condition based on the friendship (happiness) level of the trigger Pokémon.
 * Typically used for Marks that reward a strong bond between trainer and Pokémon.
 *
 * @author Darcosse
 * @version 1.0
 * @since 2026
 */
public class FriendshipCondition implements MarkCondition {

    private final int requiredFriendship;

    /**
     * Constructs a FriendshipCondition with a specific threshold.
     * * @param requiredFriendship The minimum friendship value required (usually 0-255).
     */
    public FriendshipCondition(int requiredFriendship) {
        this.requiredFriendship = requiredFriendship;
    }

    /**
     * Checks if the Pokémon's current friendship score is greater than or equal
     * to the required threshold.
     */
    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        int friendship = triggerPokemon.getFriendship();
        return friendship >= requiredFriendship;
    }

    /**
     * Returns null as friendship is a native Pokémon property and doesn't
     * require an external NBT counter.
     */
    @Override
    public String getNbtKey() { return null; }

    /**
     * Friendship conditions are checked as a single state requirement.
     */
    @Override
    public int getRequiredCount() { return 1; }

    /**
     * Gets the required friendship value for UI display and tooltips.
     */
    public int getRequiredFriendship() { return requiredFriendship; }
}