package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * Condition checking the current status effect (e.g., Poison, Burn, Sleep)
 * of the player's Pokémon. It uses the Showdown-compatible ID for matching.
 *
 * @author Darcosse
 * @version 1.0
 * @since 2026
 */
public class StatusCondition implements MarkCondition {

    private final List<String> requiredStatuses;

    /**
     * Constructs a StatusCondition with a list of valid status identifiers.
     * * @param requiredStatuses List of status IDs (e.g., "psn", "brn", "slp", "frz", "par").
     */
    public StatusCondition(List<String> requiredStatuses) {
        this.requiredStatuses = requiredStatuses;
    }

    /**
     * Validates if the player's Pokémon is currently affected by one of the required statuses.
     * It checks the internal Cobblemon status container and compares the Showdown name.
     */
    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        // We verify the status of OUR Pokémon (the trigger)
        var statusContainer = triggerPokemon.getStatus();
        if (statusContainer == null) return false;

        var status = statusContainer.getStatus();
        if (status == null) return false;

        // The showdownName is the short ID: "par", "psn", "brn", "slp", "frz", "tox"
        String statusId = status.getShowdownName().toLowerCase();
        return requiredStatuses.contains(statusId);
    }

    /**
     * Returns null as status checks are volatile and don't require persistent NBT tracking.
     */
    @Override
    public String getNbtKey() { return null; }

    /**
     * Status conditions are state-based and satisfied with a single successful match.
     */
    @Override
    public int getRequiredCount() { return 1; }

    /**
     * Gets the list of required status IDs for UI description and tooltips.
     */
    public List<String> getRequiredStatuses() { return requiredStatuses; }
}