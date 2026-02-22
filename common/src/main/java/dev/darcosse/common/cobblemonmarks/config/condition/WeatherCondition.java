package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * Condition based on the current weather in the player's location.
 * It distinguishes between clear skies, rain, thunder, and snow by
 * checking both world state and local biome precipitation.
 *
 * @author Darcosse
 * @version 1.0
 * @since 2026
 */
public class WeatherCondition implements MarkCondition {

    /**
     * Enumeration of supported weather types.
     */
    public enum Weather { CLEAR, RAIN, THUNDER, SNOW }

    private final List<Weather> requiredWeathers;

    /**
     * Constructs a WeatherCondition with a list of acceptable weather states.
     * * @param requiredWeathers List of Weather enums; at least one must match for the condition to be met.
     */
    public WeatherCondition(List<Weather> requiredWeathers) {
        this.requiredWeathers = requiredWeathers;
    }

    /**
     * Validates the current weather at the player's position.
     * Logic correctly identifies Snow by checking if it's raining in a cold biome.
     */
    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        var level = player.serverLevel();
        boolean isThunder = level.isThundering();
        boolean isRain = level.isRaining();

        // Snow check: In Minecraft, snow occurs when it rains in a biome
        // where precipitation is set to SNOW at the current height/position.
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

    /**
     * Returns null as weather checks are instantaneous and do not require NBT tracking.
     */
    @Override
    public String getNbtKey() { return null; }

    /**
     * Weather conditions are binary; one successful check is sufficient.
     */
    @Override
    public int getRequiredCount() { return 1; }

    /**
     * Gets the list of required weathers for UI and tooltips.
     */
    public List<Weather> getWeathers() { return requiredWeathers; }
}