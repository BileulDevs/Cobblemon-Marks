package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * Condition triggered by defeating Pokémon with specific regional or aesthetic forms.
 * It checks the target Pokémon's aspects for keywords such as "alolan", "hisuian", etc.
 *
 * @author Darcosse
 * @version 1.0
 * @since 2026
 */
public class FormKillCondition extends KillCondition {

    private final List<String> requiredForms;

    /**
     * Constructs a FormKillCondition with a required defeat count and a list of valid form names.
     *
     * @param requiredKills Number of specific forms to defeat to satisfy the condition.
     * @param requiredForms List of strings representing the target forms (e.g., "alolan", "galarian").
     * @param nbtKey The NBT key used to persist progress on the Pokémon.
     */
    public FormKillCondition(int requiredKills, List<String> requiredForms, String nbtKey) {
        super(requiredKills, List.of(), List.of(), nbtKey);
        this.requiredForms = requiredForms;
    }

    /**
     * Validates if the defeated Pokémon possesses any of the required form aspects.
     * Aspects in Cobblemon are stored as strings; this check is case-insensitive.
     */
    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        if (targetPokemon == null) return false;

        List<String> targetAspects = targetPokemon.getAspects().stream().toList();
        for (String form : requiredForms) {
            if (targetAspects.contains(form.toLowerCase())) return true;
        }
        return false;
    }

    /**
     * Returns the list of required forms for UI description and tooltips.
     */
    public List<String> getRequiredForms() { return requiredForms; }
}