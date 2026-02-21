package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class DimensionCondition implements MarkCondition {

    private final List<String> requiredDimensions; // ex: "minecraft:the_nether"

    public DimensionCondition(List<String> requiredDimensions) {
        this.requiredDimensions = requiredDimensions;
    }

    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        ResourceLocation currentDimension = player.serverLevel().dimension().location();
        return requiredDimensions.stream()
                .map(ResourceLocation::parse)
                .anyMatch(currentDimension::equals);
    }

    @Override
    public String getNbtKey() { return null; }

    @Override
    public int getRequiredCount() { return 1; }

    public List<String> getRequiredDimensions() { return requiredDimensions; }
}