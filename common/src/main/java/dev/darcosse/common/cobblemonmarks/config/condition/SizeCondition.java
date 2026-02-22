package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * Condition based on the physical size (scale) of a Pokémon.
 * It maps the floating-point scale ratio to a discrete Size enum, allowing
 * for Marks dedicated to miniature or giant Pokémon.
 *
 * @author Darcosse
 * @version 1.0
 * @since 2026
 */
public class SizeCondition implements MarkCondition {

    /**
     * Enumeration of possible Pokémon sizes based on their scale modifier.
     */
    public enum Size {
        XXXS, XXS, XS, S, M, L, XL, XXL, XXXL
    }

    private final List<Size> requiredSizes;
    private final boolean checkTarget;

    /**
     * Constructs a SizeCondition targeting the opponent/target Pokémon by default.
     */
    public SizeCondition(List<Size> requiredSizes) {
        this(requiredSizes, true);
    }

    /**
     * Constructs a SizeCondition with an option to check either the player's
     * Pokémon or the target.
     */
    public SizeCondition(List<Size> requiredSizes, boolean checkTarget) {
        this.requiredSizes = requiredSizes;
        this.checkTarget = checkTarget;
    }

    /**
     * Converts a scale ratio into a discrete Size category.
     * Note: In Cobblemon, a higher ratio often indicates a smaller Pokémon
     * depending on the base scale implementation.
     */
    private Size scaleToSize(float ratio) {
        if (ratio >= 1.5f)       return Size.XXXS;
        else if (ratio >= 1.3f)  return Size.XXS;
        else if (ratio >= 1.15f) return Size.XS;
        else if (ratio >= 1.05f) return Size.S;
        else if (ratio >= 0.95f) return Size.M;
        else if (ratio >= 0.85f) return Size.L;
        else if (ratio >= 0.7f)  return Size.XL;
        else if (ratio >= 0.5f)  return Size.XXL;
        else                     return Size.XXXL;
    }

    /**
     * Validates if the selected Pokémon falls within the required size categories.
     */
    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        Pokemon pokemon = checkTarget ? targetPokemon : triggerPokemon;
        if (pokemon == null) return false;

        float ratio = pokemon.getScaleModifier() / pokemon.getSpecies().getBaseScale();
        Size size = scaleToSize(ratio);
        return requiredSizes.contains(size);
    }

    /**
     * Returns null as size is an inherent property and does not require NBT counters.
     */
    @Override
    public String getNbtKey() { return null; }

    /**
     * Size conditions are binary requirements satisfied in a single check.
     */
    @Override
    public int getRequiredCount() { return 1; }

    public List<Size> getRequiredSizes() { return requiredSizes; }
    public boolean isCheckTarget()       { return checkTarget; }
}