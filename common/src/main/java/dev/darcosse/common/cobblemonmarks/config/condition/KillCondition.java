package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class KillCondition implements MarkCondition {

    private final int requiredKills;
    private final List<String> requiredTypes;
    private final List<String> requiredSpecies;
    private final String nbtKey;

    public KillCondition(int requiredKills, List<String> requiredTypes,
                         List<String> requiredSpecies, String nbtKey) {
        this.requiredKills = requiredKills;
        this.requiredTypes = requiredTypes;
        this.requiredSpecies = requiredSpecies;
        this.nbtKey = nbtKey;
    }

    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        if (targetPokemon == null) return false;

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

        if (!requiredSpecies.isEmpty()) {
            String speciesName = targetPokemon.getSpecies().getName().toLowerCase();
            if (!requiredSpecies.contains(speciesName)) return false;
        }

        return true;
    }

    @Override
    public String getNbtKey() { return nbtKey; }

    @Override
    public int getRequiredCount() { return requiredKills; }

    // Getters nécessaires pour la sérialisation
    public List<String> getRequiredTypes()   { return requiredTypes; }
    public List<String> getRequiredSpecies() { return requiredSpecies; }
}