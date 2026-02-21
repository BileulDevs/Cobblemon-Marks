package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

// Dans TimeOfBattleCondition.isMet(), pas besoin de persistentData
// On lit directement le nombre de tours depuis le pokémon killer
// Le handler passe le turn count via une variable temporaire

public class TimeOfBattleCondition implements MarkCondition {

    private final Integer minTurns;
    private final Integer maxTurns;

    public TimeOfBattleCondition(Integer minTurns, Integer maxTurns) {
        this.minTurns = minTurns;
        this.maxTurns = maxTurns;
    }

    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        // Le turn count est stocké temporairement dans le persistentData par le handler
        int turns = triggerPokemon.getPersistentData().getInt(TURN_COUNT_KEY);
        if (minTurns != null && turns < minTurns) return false;
        if (maxTurns != null && turns > maxTurns) return false;
        return true;
    }

    public static final String TURN_COUNT_KEY = "markfarm_battle_turns";

    @Override
    public String getNbtKey() { return null; }

    @Override
    public int getRequiredCount() { return 1; }
}