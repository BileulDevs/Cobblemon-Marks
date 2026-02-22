package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

/**
 * Condition based on the level of the target Pokémon.
 * It can enforce a specific level range or require the target to be
 * stronger than the player's Pokémon (power scaling).
 *
 * @author Darcosse
 * @version 1.0
 * @since 2026
 */
public class LevelCondition implements MarkCondition {

    private final Integer minLevel;
    private final Integer maxLevel;
    private final boolean mustBeStrongerThanKiller;

    /**
     * Constructs a LevelCondition with a specific level range.
     */
    public LevelCondition(Integer minLevel, Integer maxLevel) {
        this(minLevel, maxLevel, false);
    }

    /**
     * Constructs a LevelCondition focused on the level difference between
     * the killer and the target.
     */
    public LevelCondition(boolean mustBeStrongerThanKiller) {
        this(null, null, mustBeStrongerThanKiller);
    }

    public LevelCondition(Integer minLevel, Integer maxLevel,
                          boolean mustBeStrongerThanKiller) {
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.mustBeStrongerThanKiller = mustBeStrongerThanKiller;
    }

    /**
     * Validates if the target Pokémon meets the level requirements.
     * If 'mustBeStrongerThanKiller' is true, it compares levels; otherwise,
     * it checks against absolute bounds.
     */
    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        if (targetPokemon == null) return false;

        if (mustBeStrongerThanKiller) {
            int killerLevel = triggerPokemon.getLevel();
            int targetLevel = targetPokemon.getLevel();

            // Special case: if the player's Pokémon is at max level (100),
            // level 100 targets are accepted as "stronger" or equal.
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

    /**
     * Returns null as level checks are performed instantly without a counter.
     */
    @Override
    public String getNbtKey() { return null; }

    /**
     * Level conditions are threshold requirements, met in a single instance.
     */
    @Override
    public int getRequiredCount() { return 1; }

    public Integer getMinLevel()              { return minLevel; }
    public Integer getMaxLevel()              { return maxLevel; }
    public boolean isMustBeStrongerThanKiller() { return mustBeStrongerThanKiller; }
}