package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class StatusCondition implements MarkCondition {

    private final List<String> requiredStatuses; // "poison", "burn", "sleep", "freeze", "paralysis"

    public StatusCondition(List<String> requiredStatuses) {
        this.requiredStatuses = requiredStatuses;
    }

    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        // On vérifie le statut de NOTRE pokémon (triggerPokemon)
        var statusContainer = triggerPokemon.getStatus();
        if (statusContainer == null) return false;

        var status = statusContainer.getStatus();
        if (status == null) return false;

        // Le showdownId est le nom court : "par", "psn", "brn", "slp", "frz", "tox"
        String statusId = status.getShowdownName().toLowerCase();
        return requiredStatuses.contains(statusId);
    }

    @Override
    public String getNbtKey() { return null; }

    @Override
    public int getRequiredCount() { return 1; }

    public List<String> getRequiredStatuses() { return requiredStatuses; }
}