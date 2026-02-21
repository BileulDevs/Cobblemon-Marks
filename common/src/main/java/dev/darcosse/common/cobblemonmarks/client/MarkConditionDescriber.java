package dev.darcosse.common.cobblemonmarks.client;

import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.darcosse.common.cobblemonmarks.config.MarksCondition;
import dev.darcosse.common.cobblemonmarks.config.condition.*;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;

public class MarkConditionDescriber {

    public static List<Component> describe(MarksCondition markCondition, Pokemon pokemon) {
        List<Component> lines = new ArrayList<>();
        var conditions = markCondition.getConditions();
        KillCondition killCond = conditions.getKillCondition();

        // Ligne 1 : progression
        if (killCond != null) {
            int current = pokemon.getPersistentData().getInt(killCond.getNbtKey());
            int required = killCond.getRequiredCount();
            lines.add(Component.literal("§eProgression : §f" + current + "/" + required));
        }

        // Ligne 2+ : description des conditions
        if (killCond instanceof StreakCondition) {
            lines.add(Component.literal("§7Gagner §f" + killCond.getRequiredCount() + " §7combats d'affilée sans tomber"));
        } else if (killCond instanceof FishingKillCondition) {
            lines.add(Component.literal("§7Tuer §f" + killCond.getRequiredCount() + " §7pokémon pêchés"));
        } else if (killCond != null) {
            StringBuilder sb = new StringBuilder("§7Tuer §f" + killCond.getRequiredCount() + " §7pokémon");
            if (!killCond.getRequiredTypes().isEmpty())
                sb.append(" de type §f").append(String.join("§7/§f", killCond.getRequiredTypes()));
            if (!killCond.getRequiredSpecies().isEmpty())
                sb.append(" (§f").append(String.join("§7, §f", killCond.getRequiredSpecies())).append("§7)");
            lines.add(Component.literal(sb.toString()));
        }

        // Conditions required
        for (MarkCondition c : conditions.getRequired()) {
            lines.add(Component.literal("§a✔ " + describeCondition(c)));
        }

        // Conditions excluded
        for (MarkCondition c : conditions.getExcluded()) {
            lines.add(Component.literal("§c✘ " + describeCondition(c)));
        }

        // Death condition dans required
        for (MarkCondition c : conditions.getRequired()) {
            if (c instanceof DeathCondition dc) {
                int current = pokemon.getPersistentData().getInt(dc.getNbtKey());
                lines.add(0, Component.literal("§eProgression : §f" + current + "/" + dc.getRequiredCount()));
            }
        }

        return lines;
    }

    private static String describeCondition(MarkCondition c) {
        if (c instanceof WeatherCondition wc) {
            return "Météo : " + wc.getWeathers().stream()
                    .map(w -> switch (w) {
                        case RAIN -> "Pluie";
                        case THUNDER -> "Orage";
                        case SNOW -> "Neige";
                        case CLEAR -> "Clair";
                    })
                    .reduce((a, b) -> a + "/" + b).orElse("?");
        }
        if (c instanceof BiomeCondition bc) return "Biome : " + String.join(", ", bc.getRequiredBiomes());
        if (c instanceof TimeCondition tc) return "Heure : " + tc.getMinTime() + "-" + tc.getMaxTime();
        if (c instanceof LevelCondition lc) {
            if (lc.isMustBeStrongerThanKiller()) return "Adversaire plus fort que nous";
            return "Niveau adversaire : " + (lc.getMinLevel() != null ? lc.getMinLevel() : "?")
                    + "-" + (lc.getMaxLevel() != null ? lc.getMaxLevel() : "max");
        }
        if (c instanceof DimensionCondition dc) return "Dimension : " + String.join(", ", dc.getRequiredDimensions());
        if (c instanceof StatusCondition sc) return "Statut adversaire : " + String.join(", ", sc.getRequiredStatuses());
        if (c instanceof SizeCondition sc) return "Taille : " + sc.getRequiredSizes().stream()
                .map(Enum::name).reduce((a, b) -> a + "/" + b).orElse("?");
        if (c instanceof FriendshipCondition fc) return "Amitié ≥ " + fc.getRequiredFriendship();
        if (c instanceof ShinyCondition) return "Capturer un pokémon shiny en combat";
        return c.getClass().getSimpleName();
    }
}