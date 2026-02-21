package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class StreakCondition extends KillCondition {

    public StreakCondition(int requiredStreak, String nbtKey) {
        super(requiredStreak, List.of(), List.of(), nbtKey);
    }

    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        return true; // pas de filtre type/species, toute victoire compte
    }
}