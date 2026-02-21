package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.darcosse.common.cobblemonmarks.CobblemonMarksMod;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class SizeCondition implements MarkCondition {

    public enum Size {
        XXXS, XXS, XS, S, M, L, XL, XXL, XXXL
    }

    private final List<Size> requiredSizes;
    private final boolean checkTarget; // true = vaincu, false = notre pokémon

    public SizeCondition(List<Size> requiredSizes) {
        this(requiredSizes, true);
    }

    public SizeCondition(List<Size> requiredSizes, boolean checkTarget) {
        this.requiredSizes = requiredSizes;
        this.checkTarget = checkTarget;
    }

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

    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        Pokemon pokemon = checkTarget ? targetPokemon : triggerPokemon;
        if (pokemon == null) return false;

        float ratio = pokemon.getScaleModifier() / pokemon.getSpecies().getBaseScale();
        Size size = scaleToSize(ratio);
        return requiredSizes.contains(size);
    }

    @Override
    public String getNbtKey() { return null; }

    @Override
    public int getRequiredCount() { return 1; }

    public List<Size> getRequiredSizes() { return requiredSizes; }
    public boolean isCheckTarget()       { return checkTarget; }
}