package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

/**
 * Condition triggered by the Pokémon's own defeat (fainting).
 * This condition is unique as it tracks how many times the holder has fainted
 * to satisfy a specific Mark requirement.
 *
 * @author Darcosse
 * @version 1.0
 * @since 2026
 */
public class DeathCondition implements MarkCondition {

    private final int requiredDeaths;
    private final String nbtKey;

    /**
     * Constructs a DeathCondition with a specific KO requirement and NBT storage key.
     * * @param requiredDeaths Total number of times the Pokémon must faint.
     * @param nbtKey The NBT key used to persist the death counter on the Pokémon.
     */
    public DeathCondition(int requiredDeaths, String nbtKey) {
        this.requiredDeaths = requiredDeaths;
        this.nbtKey = nbtKey;
    }

    /**
     * Validation logic for the death event.
     * Always returns true when called, as the actual counter increment and
     * threshold check are managed globally by the MarksHandler.
     */
    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        return true;
    }

    /**
     * Returns the NBT key used to store the current death count.
     */
    @Override
    public String getNbtKey() { return nbtKey; }

    /**
     * Returns the total number of KOs required to fulfill this condition.
     */
    @Override
    public int getRequiredCount() { return requiredDeaths; }
}