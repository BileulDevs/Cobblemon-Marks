package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

public class DeathCondition implements MarkCondition {

    private final int requiredDeaths;
    private final String nbtKey;

    public DeathCondition(int requiredDeaths, String nbtKey) {
        this.requiredDeaths = requiredDeaths;
        this.nbtKey = nbtKey;
    }

    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        // La vérification du compteur est gérée dans le handler,
        // isMet ici sert juste à confirmer que c'est bien ce pokémon
        return true;
    }

    @Override
    public String getNbtKey() { return nbtKey; }

    @Override
    public int getRequiredCount() { return requiredDeaths; }
}