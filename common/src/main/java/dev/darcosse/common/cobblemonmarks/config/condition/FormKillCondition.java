package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class FormKillCondition extends KillCondition {

    private final List<String> requiredForms; // ex: "alolan", "galarian", "hisuian", "paldean"

    public FormKillCondition(int requiredKills, List<String> requiredForms, String nbtKey) {
        super(requiredKills, List.of(), List.of(), nbtKey);
        this.requiredForms = requiredForms;
    }

    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        if (targetPokemon == null) return false;

        List<String> targetAspects = targetPokemon.getAspects().stream().toList();
        for (String form : requiredForms) {
            if (targetAspects.contains(form.toLowerCase())) return true;
        }
        return false;
    }

    public List<String> getRequiredForms() { return requiredForms; }
}