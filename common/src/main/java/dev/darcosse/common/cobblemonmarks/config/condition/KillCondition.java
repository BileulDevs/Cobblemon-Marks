package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * Standard condition triggered by defeating an opponent Pokémon.
 * Supports filtering by specific types, species, and a required defeat count.
 * This class serves as the foundation for most combat-oriented Marks.
 *
 * @author Darcosse
 * @version 1.0
 * @since 2026
 */
public class KillCondition implements MarkCondition {

    private final int requiredKills;
    private final List<String> requiredTypes;
    private final List<String> requiredSpecies;
    private final String nbtKey;

    /**
     * Constructs a KillCondition with filtering parameters and progress tracking.
     *
     * @param requiredKills   The number of defeats needed to satisfy the condition.
     * @param requiredTypes   List of types the target must have (e.g., "fire", "water").
     * @param requiredSpecies List of species the target must belong to (e.g., "pikachu").
     * @param nbtKey          The NBT key used to store and retrieve the persistent counter.
     */
    public KillCondition(int requiredKills, List<String> requiredTypes,
                         List<String> requiredSpecies, String nbtKey) {
        this.requiredKills = requiredKills;
        this.requiredTypes = requiredTypes;
        this.requiredSpecies = requiredSpecies;
        this.nbtKey = nbtKey;
    }

    /**
     * Validates if the defeated target Pokémon matches the configured filters.
     * Checks for type overlap and species exact matches.
     */
    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        if (targetPokemon == null) return false;

        // Type filtering: Target must share at least one type from the required list
        if (!requiredTypes.isEmpty()) {
            boolean typeMatch = false;
            for (var type : targetPokemon.getSpecies().getTypes()) {
                if (requiredTypes.contains(type.getName().toLowerCase())) {
                    typeMatch = true;
                    break;
                }
            }
            if (!typeMatch) return false;
        }

        // Species filtering: Target must belong to one of the specific species IDs
        if (!requiredSpecies.isEmpty()) {
            String speciesName = targetPokemon.getSpecies().getName().toLowerCase();
            if (!requiredSpecies.contains(speciesName)) return false;
        }

        return true;
    }

    /**
     * Returns the NBT key used to track the defeat count for this condition.
     */
    @Override
    public String getNbtKey() { return nbtKey; }

    /**
     * Returns the target number of defeats required.
     */
    @Override
    public int getRequiredCount() { return requiredKills; }

    /**
     * Returns the list of types required for this condition (used for UI and serialization).
     */
    public List<String> getRequiredTypes()   { return requiredTypes; }

    /**
     * Returns the list of species required for this condition (used for UI and serialization).
     */
    public List<String> getRequiredSpecies() { return requiredSpecies; }
}