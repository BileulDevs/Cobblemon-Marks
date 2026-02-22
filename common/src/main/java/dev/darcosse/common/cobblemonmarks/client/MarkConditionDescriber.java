package dev.darcosse.common.cobblemonmarks.client;

import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.darcosse.common.cobblemonmarks.config.MarksCondition;
import dev.darcosse.common.cobblemonmarks.config.condition.*;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;

public class MarkConditionDescriber {

    // Couleurs des types Pokémon
    private static String getTypeColor(String type) {
        return switch (type.toLowerCase()) {
            case "normal"   -> "§f";
            case "fire"     -> "§c";
            case "water"    -> "§9";
            case "electric" -> "§e";
            case "grass"    -> "§a";
            case "ice"      -> "§b";
            case "fighting" -> "§4";
            case "poison"   -> "§5";
            case "ground"   -> "§6";
            case "flying"   -> "§3";
            case "psychic"  -> "§d";
            case "bug"      -> "§2";
            case "rock"     -> "§8";
            case "ghost"    -> "§5";
            case "dragon"   -> "§1";
            case "dark"     -> "§8";
            case "steel"    -> "§7";
            case "fairy"    -> "§d";
            default         -> "§f";
        };
    }

    public static List<Component> describe(MarksCondition markCondition, Pokemon pokemon) {
        List<Component> lines = new ArrayList<>();
        var conditions = markCondition.getConditions();
        KillCondition killCond = conditions.getKillCondition();

        if (killCond != null) {
            int current = pokemon.getPersistentData().getInt(killCond.getNbtKey());
            int required = killCond.getRequiredCount();
            lines.add(Component.literal("§eProgress: §f" + current + "§7/§f" + required));
        }

        if (killCond instanceof StreakCondition) {
            lines.add(Component.literal("§7Win §f" + killCond.getRequiredCount() + " §7battles in a row without fainting"));
        } else if (killCond instanceof FishingKillCondition) {
            lines.add(Component.literal("§7Defeat §f" + killCond.getRequiredCount() + " §7fished Pokémon"));
        } else if (killCond != null) {
            List<String> translatedSpecies = killCond.getRequiredSpecies().stream()
                    .map(s -> Component.translatable("cobblemon.species." + s.toLowerCase() + ".name").getString())
                    .toList();

            String coloredTypes = killCond.getRequiredTypes().stream()
                    .map(t -> getTypeColor(t) + t)
                    .reduce((a, b) -> a + "§7/§f" + b).orElse("");

            if (!killCond.getRequiredTypes().isEmpty() && !killCond.getRequiredSpecies().isEmpty()) {
                lines.add(Component.literal("§7Defeat §f" + killCond.getRequiredCount()
                        + " " + coloredTypes + "§7-type Pokémon among:"));
                for (int i = 0; i < translatedSpecies.size(); i += 4) {
                    List<String> chunk = translatedSpecies.subList(i, Math.min(i + 4, translatedSpecies.size()));
                    lines.add(Component.literal("§f  " + String.join("§7, §f", chunk)));
                }
            } else if (!killCond.getRequiredTypes().isEmpty()) {
                lines.add(Component.literal("§7Defeat §f" + killCond.getRequiredCount()
                        + " " + coloredTypes + "§7-type Pokémon"));
            } else if (!killCond.getRequiredSpecies().isEmpty()) {
                lines.add(Component.literal("§7Defeat §f" + killCond.getRequiredCount() + " §7Pokémon among:"));
                for (int i = 0; i < translatedSpecies.size(); i += 4) {
                    List<String> chunk = translatedSpecies.subList(i, Math.min(i + 4, translatedSpecies.size()));
                    lines.add(Component.literal("§f  " + String.join("§7, §f", chunk)));
                }
            } else {
                lines.add(Component.literal("§7Defeat §f" + killCond.getRequiredCount() + " §7Pokémon"));
            }
        }

        for (MarkCondition c : conditions.getRequired()) {
            lines.add(Component.literal("§a✔ ").append(describeCondition(c)));
        }

        for (MarkCondition c : conditions.getExcluded()) {
            lines.add(Component.literal("§c✘ ").append(describeCondition(c)));
        }

        for (MarkCondition c : conditions.getRequired()) {
            if (c instanceof DeathCondition dc) {
                int current = pokemon.getPersistentData().getInt(dc.getNbtKey());
                lines.add(0, Component.literal("§eKO: §f" + current + "§7/§f" + dc.getRequiredCount()));
            }
        }

        return lines;
    }

    private static Component describeCondition(MarkCondition c) {
        if (c instanceof WeatherCondition wc) {
            String weathers = wc.getWeathers().stream()
                    .map(w -> Component.translatable("cobblemonmarks.weather." + w.name().toLowerCase()).getString())
                    .reduce((a, b) -> a + "§7/§b" + b).orElse("?");
            return Component.literal("§7Weather: §b" + weathers);
        }
        if (c instanceof BiomeCondition bc)
            return Component.literal("§7Biome: §b" + String.join("§7, §b", bc.getRequiredBiomes()));
        if (c instanceof TimeCondition tc)
            return Component.literal("§7Time: §b" + ticksToTime((int) tc.getMinTime()) + " §7- §b" + ticksToTime((int) tc.getMaxTime()));
        if (c instanceof LevelCondition lc) {
            if (lc.isMustBeStrongerThanKiller())
                return Component.literal("§7Opponent stronger than us");
            String min = lc.getMinLevel() != null ? String.valueOf(lc.getMinLevel()) : "?";
            String max = lc.getMaxLevel() != null ? String.valueOf(lc.getMaxLevel()) : "max";
            return Component.literal("§7Opponent level: §b" + min + "§7-§b" + max);
        }
        if (c instanceof DimensionCondition dc)
            return Component.literal("§7Dimension: §b" + String.join("§7, §b", dc.getRequiredDimensions()));
        if (c instanceof StatusCondition sc)
            return Component.literal("§7Opponent status: §b" + String.join("§7, §b", sc.getRequiredStatuses()));
        if (c instanceof SizeCondition sc) {
            String sizes = sc.getRequiredSizes().stream()
                    .map(s -> Component.translatable("cobblemonmarks.size." + s.name().toLowerCase()).getString())
                    .reduce((a, b) -> a + "§7/§b" + b).orElse("?");
            return Component.literal("§7Size: §b" + sizes);
        }
        if (c instanceof FriendshipCondition fc)
            return Component.literal("§7Friendship §b≥ §f" + fc.getRequiredFriendship());
        if (c instanceof ShinyCondition)
            return Component.literal("§7Catch a §6shiny §7Pokémon in battle");
        return Component.literal("§7" + c.getClass().getSimpleName());
    }

    private static String ticksToTime(int ticks) {
        int totalMinutes = (int) (((ticks + 6000) % 24000) / 24000.0 * 1440);
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return String.format("%02d:%02d", hours, minutes);
    }
}