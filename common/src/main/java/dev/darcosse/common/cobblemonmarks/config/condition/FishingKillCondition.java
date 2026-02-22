package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * Specialized KillCondition that only triggers if the target Pokémon was obtained
 * through fishing. It checks for the "fished" aspect before validating
 * species or type requirements.
 *
 * @author Darcosse
 * @version 1.0
 * @since 2026
 */
public class FishingKillCondition extends KillCondition {

    /**
     * Constructs a FishingKillCondition with specific requirements and progress tracking.
     * * @param requiredKills Number of fished Pokémon to defeat.
     * @param requiredTypes Optional list of types the fished Pokémon must have.
     * @param requiredSpecies Optional list of species the fished Pokémon must be.
     * @param nbtKey The NBT key used to persist progress.
     */
    public FishingKillCondition(int requiredKills, List<String> requiredTypes,
                                List<String> requiredSpecies, String nbtKey) {
        super(requiredKills, requiredTypes, requiredSpecies, nbtKey);
    }

    /**
     * Validates the condition. First ensures the target has the "fished" aspect,
     * then delegates type and species checks to the parent KillCondition.
     */
    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        if (targetPokemon == null) return false;

        // In Cobblemon, fished Pokémon are tagged with the "fished" aspect.
        if (!targetPokemon.getForcedAspects().contains("fished")) return false;

        // Delegate species and type filtering to the standard KillCondition logic.
        return super.isMet(triggerPokemon, targetPokemon, player);
    }
}