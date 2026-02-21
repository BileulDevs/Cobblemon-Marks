package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class WeatherCondition implements MarkCondition {

    public enum Weather { CLEAR, RAIN, THUNDER, SNOW }

    private final List<Weather> requiredWeathers; // au moins une doit matcher

    public WeatherCondition(List<Weather> requiredWeathers) {
        this.requiredWeathers = requiredWeathers;
    }

    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        var level = player.serverLevel();
        boolean isThunder = level.isThundering();
        boolean isRain = level.isRaining();
        boolean isSnow = level.getBiome(player.blockPosition()).value()
                .getPrecipitationAt(player.blockPosition())
                == net.minecraft.world.level.biome.Biome.Precipitation.SNOW && isRain;

        for (Weather w : requiredWeathers) {
            switch (w) {
                case CLEAR   -> { if (!isRain && !isThunder) return true; }
                case RAIN    -> { if (isRain && !isThunder && !isSnow) return true; }
                case THUNDER -> { if (isThunder) return true; }
                case SNOW    -> { if (isSnow) return true; }
            }
        }
        return false;
    }

    @Override
    public String getNbtKey() { return null; } // instantané, pas de compteur

    @Override
    public int getRequiredCount() { return 1; }

    public List<Weather> getWeathers() { return requiredWeathers; }
}