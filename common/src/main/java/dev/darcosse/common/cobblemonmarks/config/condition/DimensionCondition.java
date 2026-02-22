package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * Condition that checks if the player is currently in one of the specified dimensions.
 * Used to create dimension-specific Marks (e.g., Nether-only or End-only insignias).
 *
 * @author Darcosse
 * @version 1.0
 * @since 2026
 */
public class DimensionCondition implements MarkCondition {

    private final List<String> requiredDimensions;

    /**
     * Constructs a DimensionCondition with a list of valid dimension identifiers.
     * * @param requiredDimensions List of dimension strings (e.g., "minecraft:overworld", "minecraft:the_nether").
     */
    public DimensionCondition(List<String> requiredDimensions) {
        this.requiredDimensions = requiredDimensions;
    }

    /**
     * Validates if the player's current dimension matches any of the required identifiers.
     */
    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        ResourceLocation currentDimension = player.serverLevel().dimension().location();
        return requiredDimensions.stream()
                .map(ResourceLocation::parse)
                .anyMatch(currentDimension::equals);
    }

    /**
     * Returns null as dimension checks are instantaneous and do not require NBT tracking.
     */
    @Override
    public String getNbtKey() { return null; }

    /**
     * Dimension conditions are binary requirements, satisfied with a single successful check.
     */
    @Override
    public int getRequiredCount() { return 1; }

    /**
     * Gets the list of valid dimensions for UI display and tooltips.
     */
    public List<String> getRequiredDimensions() { return requiredDimensions; }
}