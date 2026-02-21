package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

public class LevelCondition implements MarkCondition {

    private final Integer minLevel;  // null = pas de minimum
    private final Integer maxLevel;  // null = pas de maximum
    private final boolean mustBeStrongerThanKiller; // vaincu plus fort que notre pokémon

    public LevelCondition(Integer minLevel, Integer maxLevel) {
        this(minLevel, maxLevel, false);
    }

    public LevelCondition(boolean mustBeStrongerThanKiller) {
        this(null, null, mustBeStrongerThanKiller);
    }

    public LevelCondition(Integer minLevel, Integer maxLevel,
                          boolean mustBeStrongerThanKiller) {
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.mustBeStrongerThanKiller = mustBeStrongerThanKiller;
    }

    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        if (targetPokemon == null) return false;

        if (mustBeStrongerThanKiller) {
            int killerLevel = triggerPokemon.getLevel();
            int targetLevel = targetPokemon.getLevel();
            // Si notre pokémon est lvl 100, les pokémon lvl 100 comptent aussi
            if (killerLevel == 100) {
                return targetLevel >= killerLevel;
            }
            return targetLevel > killerLevel;
        }

        int level = targetPokemon.getLevel();
        if (minLevel != null && level < minLevel) return false;
        if (maxLevel != null && level > maxLevel) return false;
        return true;
    }

    @Override
    public String getNbtKey() { return null; }

    @Override
    public int getRequiredCount() { return 1; }

    public Integer getMinLevel()              { return minLevel; }
    public Integer getMaxLevel()              { return maxLevel; }
    public boolean isMustBeStrongerThanKiller() { return mustBeStrongerThanKiller; }
}