package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class FormKillCondition extends KillCondition {

    private final List<String> requiredForms;

    public FormKillCondition(int requiredKills, List<String> requiredTypes,
                             List<String> requiredSpecies, List<String> requiredForms, String nbtKey) {
        super(requiredKills, requiredTypes, requiredSpecies, nbtKey);
        this.requiredForms = requiredForms;
    }

    public List<String> getRequiredForms() { return requiredForms; }

    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        if (targetPokemon == null) return false;

        if (!requiredForms.isEmpty()) {
            String form = targetPokemon.getForm().getName().toLowerCase();
            if (!requiredForms.contains(form)) return false;
        }

        return super.isMet(triggerPokemon, targetPokemon, player);
    }
}