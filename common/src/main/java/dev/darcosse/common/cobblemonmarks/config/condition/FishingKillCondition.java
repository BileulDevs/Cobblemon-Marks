package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.api.mark.Marks;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class FishingKillCondition extends KillCondition {

    public FishingKillCondition(int requiredKills, List<String> requiredTypes,
                                List<String> requiredSpecies, String nbtKey) {
        super(requiredKills, requiredTypes, requiredSpecies, nbtKey);
    }

    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        if (targetPokemon == null) return false;

        // Les pokémon pêchés ont l'aspect "fished" dans leurs forcedAspects
        if (!targetPokemon.getForcedAspects().contains("fished")) return false;

        // Déléguer le reste à KillCondition (types + species)
        return super.isMet(triggerPokemon, targetPokemon, player);
    }
}