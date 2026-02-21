package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;

import java.util.List;

public class BiomeCondition implements MarkCondition {

    private final List<String> requiredBiomes; // "minecraft:desert" ou "#cobblemon:is_arid"

    public BiomeCondition(List<String> requiredBiomes) {
        this.requiredBiomes = requiredBiomes;
    }

    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        var level = player.serverLevel();
        var pos = player.blockPosition();
        var biomeHolder = level.getBiome(pos);

        for (String entry : requiredBiomes) {
            if (entry.startsWith("#")) {
                // C'est un tag — ex: "#cobblemon:is_arid" ou "#minecraft:is_jungle"
                ResourceLocation tagId = ResourceLocation.parse(entry.substring(1));
                var tagKey = TagKey.create(Registries.BIOME, tagId);
                if (biomeHolder.is(tagKey)) return true;
            } else {
                // C'est un biome direct — ex: "minecraft:desert"
                ResourceLocation biomeId = ResourceLocation.parse(entry);
                var biomeKey = level.registryAccess()
                        .registryOrThrow(Registries.BIOME)
                        .getKey(biomeHolder.value());
                if (biomeId.equals(biomeKey)) return true;
            }
        }
        return false;
    }

    @Override
    public String getNbtKey() { return null; }

    @Override
    public int getRequiredCount() { return 1; }

    public List<String> getRequiredBiomes() { return requiredBiomes; }
}