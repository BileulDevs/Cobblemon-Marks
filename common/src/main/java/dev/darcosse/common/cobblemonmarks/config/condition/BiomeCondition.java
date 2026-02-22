package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;

import java.util.List;

/**
 * Condition based on the player's current biome or biome tags.
 * Supports both direct biome IDs and biome tags (prefixed with '#').
 *
 * @author Darcosse
 * @version 1.0
 * @since 2026
 */
public class BiomeCondition implements MarkCondition {

    private final List<String> requiredBiomes;

    /**
     * Constructs a new BiomeCondition with a list of valid biomes or tags.
     * * @param requiredBiomes List of biome strings (e.g., "minecraft:desert" or "#minecraft:is_forest").
     */
    public BiomeCondition(List<String> requiredBiomes) {
        this.requiredBiomes = requiredBiomes;
    }

    /**
     * Checks if the player is currently standing in one of the required biomes or within a valid biome tag.
     */
    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        var level = player.serverLevel();
        var pos = player.blockPosition();
        var biomeHolder = level.getBiome(pos);

        for (String entry : requiredBiomes) {
            if (entry.startsWith("#")) {
                // Handle biome tags (e.g., "#cobblemon:is_arid")
                ResourceLocation tagId = ResourceLocation.parse(entry.substring(1));
                var tagKey = TagKey.create(Registries.BIOME, tagId);
                if (biomeHolder.is(tagKey)) return true;
            } else {
                // Handle direct biome IDs (e.g., "minecraft:desert")
                ResourceLocation biomeId = ResourceLocation.parse(entry);
                var biomeKey = level.registryAccess()
                        .registryOrThrow(Registries.BIOME)
                        .getKey(biomeHolder.value());
                if (biomeId.equals(biomeKey)) return true;
            }
        }
        return false;
    }

    /**
     * Returns null as this condition does not require persistent NBT tracking.
     */
    @Override
    public String getNbtKey() { return null; }

    /**
     * Biome conditions are binary (met or not met), requiring only a single success.
     */
    @Override
    public int getRequiredCount() { return 1; }

    /**
     * Gets the list of required biomes/tags for UI description purposes.
     */
    public List<String> getRequiredBiomes() { return requiredBiomes; }
}