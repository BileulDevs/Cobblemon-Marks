package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class CatchCondition extends KillCondition {

    public CatchCondition(int requiredCatches, String nbtKey) {
        super(requiredCatches, List.of(), List.of(), nbtKey);
    }

    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        return true; // le filtrage shiny est géré par ShinyCondition dans required
    }
}