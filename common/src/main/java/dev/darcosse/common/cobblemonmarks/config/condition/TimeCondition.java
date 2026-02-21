package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

public class TimeCondition implements MarkCondition {

    private final long minTime;
    private final long maxTime;

    public TimeCondition(long minTime, long maxTime) {
        this.minTime = minTime;
        this.maxTime = maxTime;
    }

    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        long time = player.serverLevel().getDayTime() % 24000;
        if (minTime <= maxTime) {
            return time >= minTime && time <= maxTime;
        } else {
            // Gère le cas qui traverse minuit ex: 22300-5999
            return time >= minTime || time <= maxTime;
        }
    }

    @Override
    public String getNbtKey() { return null; }

    @Override
    public int getRequiredCount() { return 1; }

    public long getMinTime() { return minTime; }
    public long getMaxTime() { return maxTime; }
}