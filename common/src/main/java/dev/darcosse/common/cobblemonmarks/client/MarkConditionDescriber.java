package dev.darcosse.common.cobblemonmarks.client;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.darcosse.common.cobblemonmarks.config.MarksCondition;
import dev.darcosse.common.cobblemonmarks.config.condition.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import java.util.ArrayList;
import java.util.List;

public class MarkConditionDescriber {

    private static final int WHITE   = 0xFFFFFF;
    private static final int GRAY    = 0xAAAAAA;
    private static final int YELLOW  = 0xFFAA00;
    private static final int CYAN    = 0x55FFFF;
    private static final int GOLD    = 0xFFAA00;

    private static MutableComponent white(String text)  { return Component.literal(text).withStyle(s -> s.withColor(WHITE)); }
    private static MutableComponent gray(String text)   { return Component.literal(text).withStyle(s -> s.withColor(GRAY)); }
    private static MutableComponent cyan(String text)   { return Component.literal(text).withStyle(s -> s.withColor(CYAN)); }

    private static int getTypeColorInt(String type) {
        return switch (type.toLowerCase()) {
            case "normal"   -> 0xE3E3E3;
            case "fire"     -> 0xF08030;
            case "water"    -> 0x6890F0;
            case "electric" -> 0xF8D030;
            case "grass"    -> 0x78C850;
            case "ice"      -> 0x98D8D8;
            case "fighting" -> 0xC03028;
            case "poison"   -> 0xA040A0;
            case "ground"   -> 0xE0C068;
            case "flying"   -> 0xA890F0;
            case "psychic"  -> 0xF85888;
            case "bug"      -> 0xA8B820;
            case "rock"     -> 0xB8A038;
            case "ghost"    -> 0x705898;
            case "dragon"   -> 0x7038F8;
            case "dark"     -> 0x705848;
            case "steel"    -> 0xB8B8D0;
            case "fairy"    -> 0xEE99AC;
            default         -> WHITE;
        };
    }

    private static int getStatusColorInt(String status) {
        return switch (status.toLowerCase()) {
            case "psn", "poison"    -> 0xA040A0;
            case "brn", "burn"      -> 0xF08030;
            case "par", "paralysis" -> 0xF8D030;
            case "slp", "sleep"     -> 0xAAAAAA;
            case "frz", "freeze"    -> 0x98D8D8;
            case "tox", "badpoison" -> 0x6B2D6B;
            default                 -> WHITE;
        };
    }

    private static int getSpeciesPrimaryTypeColor(String speciesName) {
        var species = PokemonSpecies.getByName(speciesName.toLowerCase());
        if (species == null) return WHITE;
        var types = species.getTypes();
        if (!types.iterator().hasNext()) return WHITE;
        return getTypeColorInt(types.iterator().next().getName().toLowerCase());
    }

    public static List<Component> describe(MarksCondition markCondition, Pokemon pokemon) {
        List<Component> lines = new ArrayList<>();
        var conditions = markCondition.getConditions();
        KillCondition killCond = conditions.getKillCondition();

        if (killCond != null) {
            int current = pokemon.getPersistentData().getInt(killCond.getNbtKey());
            int required = killCond.getRequiredCount();
            lines.add(Component.translatable("cobblemonmarks.tooltip.progress",
                    white(String.valueOf(current)),
                    gray("/"),
                    white(String.valueOf(required))
            ).withStyle(s -> s.withColor(YELLOW)));
        }

        if (killCond instanceof StreakCondition) {
            lines.add(Component.literal("🔥 ").withStyle(s -> s.withColor(0xFF6600))
                    .append(Component.translatable("cobblemonmarks.tooltip.kill.streak",
                            white(String.valueOf(killCond.getRequiredCount()))
                    ).withStyle(s -> s.withColor(GRAY))));
        } else if (killCond instanceof FishingKillCondition) {
            lines.add(Component.translatable("cobblemonmarks.tooltip.kill.fishing",
                    white(String.valueOf(killCond.getRequiredCount()))
            ).withStyle(s -> s.withColor(GRAY)));
        } else if (killCond != null) {
            List<String> translatedSpecies = killCond.getRequiredSpecies().stream()
                    .map(s -> Component.translatable("cobblemon.species." + s.toLowerCase() + ".name").getString())
                    .toList();

            // Types colorés assemblés en un seul Component
            MutableComponent coloredTypes = killCond.getRequiredTypes().stream()
                    .map(t -> (MutableComponent) Component.literal(t).withStyle(s -> s.withColor(getTypeColorInt(t))))
                    .reduce((a, b) -> a.append(gray("/")).append(b))
                    .orElse(Component.literal(""));

            if (!killCond.getRequiredTypes().isEmpty() && !killCond.getRequiredSpecies().isEmpty()) {
                lines.add(Component.translatable("cobblemonmarks.tooltip.kill.type_species_header",
                        white(String.valueOf(killCond.getRequiredCount())), coloredTypes
                ).withStyle(s -> s.withColor(GRAY)));
                for (int i = 0; i < translatedSpecies.size(); i += 4) {
                    List<String> chunk = translatedSpecies.subList(i, Math.min(i + 4, translatedSpecies.size()));
                    MutableComponent speciesLine = Component.literal("  ");
                    for (int j = 0; j < chunk.size(); j++) {
                        String originalName = killCond.getRequiredSpecies().get(i + j);
                        String translated = chunk.get(j);
                        int color = getSpeciesPrimaryTypeColor(originalName);
                        speciesLine.append(Component.literal(translated).withStyle(s -> s.withColor(color)));
                        if (j < chunk.size() - 1)
                            speciesLine.append(Component.literal(", ").withStyle(s -> s.withColor(GRAY)));
                    }
                    lines.add(speciesLine);
                }
            } else if (!killCond.getRequiredTypes().isEmpty()) {
                lines.add(Component.translatable("cobblemonmarks.tooltip.kill.type",
                        white(String.valueOf(killCond.getRequiredCount())), coloredTypes
                ).withStyle(s -> s.withColor(GRAY)));
            } else if (!killCond.getRequiredSpecies().isEmpty()) {
                lines.add(Component.translatable("cobblemonmarks.tooltip.kill.species_header",
                        white(String.valueOf(killCond.getRequiredCount()))
                ).withStyle(s -> s.withColor(GRAY)));
                for (int i = 0; i < translatedSpecies.size(); i += 4) {
                    List<String> chunk = translatedSpecies.subList(i, Math.min(i + 4, translatedSpecies.size()));
                    MutableComponent speciesLine = Component.literal("  ");
                    for (int j = 0; j < chunk.size(); j++) {
                        String originalName = killCond.getRequiredSpecies().get(i + j);
                        String translated = chunk.get(j);
                        int color = getSpeciesPrimaryTypeColor(originalName);
                        speciesLine.append(Component.literal(translated).withStyle(s -> s.withColor(color)));
                        if (j < chunk.size() - 1)
                            speciesLine.append(Component.literal(", ").withStyle(s -> s.withColor(GRAY)));
                    }
                    lines.add(speciesLine);
                }
            } else {
                lines.add(Component.translatable("cobblemonmarks.tooltip.kill.any",
                                white(String.valueOf(killCond.getRequiredCount()))
                        ).withStyle(s -> s.withColor(GRAY)));
            }
        }

        for (MarkCondition c : conditions.getRequired()) {
            if (c instanceof DeathCondition) continue;
            lines.add(Component.literal("✔ ").withStyle(s -> s.withColor(0x55FF55))
                    .append(describeCondition(c, 0x55FF55)));
        }

        for (MarkCondition c : conditions.getExcluded()) {
            lines.add(Component.literal("✘ ").withStyle(s -> s.withColor(0xFF5555))
                    .append(describeCondition(c, 0xFF5555)));
        }

        for (MarkCondition c : conditions.getRequired()) {
            if (c instanceof DeathCondition dc) {
                int current = pokemon.getPersistentData().getInt(dc.getNbtKey());
                lines.add(0, Component.literal("\uD83E\uDEA6 ").withStyle(s -> s.withColor(WHITE))
                        .append(Component.translatable("cobblemonmarks.tooltip.ko",
                                white(String.valueOf(current)),
                                gray("/"),
                                white(String.valueOf(dc.getRequiredCount()))
                        ).withStyle(s -> s.withColor(GRAY))));
            }
        }

        return lines;
    }

    private static Component describeCondition(MarkCondition c, int labelColor) {
        if (c instanceof WeatherCondition wc) {
            MutableComponent weathers = wc.getWeathers().stream()
                    .map(w -> (MutableComponent) Component.translatable("cobblemonmarks.weather." + w.name().toLowerCase())
                            .withStyle(s -> s.withColor(CYAN)))
                    .reduce((a, b) -> a.append(Component.literal("/").withStyle(s -> s.withColor(labelColor))).append(b))
                    .orElse(Component.literal("?"));
            return Component.translatable("cobblemonmarks.tooltip.condition.weather", weathers)
                    .withStyle(s -> s.withColor(labelColor));
        }
        if (c instanceof BiomeCondition bc) {
            MutableComponent biomes = bc.getRequiredBiomes().stream()
                    .map(b -> (MutableComponent) Component.literal(b).withStyle(s -> s.withColor(CYAN)))
                    .reduce((a, b) -> a.append(Component.literal(", ").withStyle(s -> s.withColor(labelColor))).append(b))
                    .orElse(Component.literal("?"));
            return Component.translatable("cobblemonmarks.tooltip.condition.biome", biomes)
                    .withStyle(s -> s.withColor(labelColor));
        }
        if (c instanceof TimeCondition tc)
            return Component.translatable("cobblemonmarks.tooltip.condition.time",
                    cyan(ticksToTime((int) tc.getMinTime())),
                    cyan(ticksToTime((int) tc.getMaxTime()))
            ).withStyle(s -> s.withColor(labelColor));
        if (c instanceof LevelCondition lc) {
            if (lc.isMustBeStrongerThanKiller())
                return Component.translatable("cobblemonmarks.tooltip.condition.level.stronger")
                        .withStyle(s -> s.withColor(labelColor));
            String min = lc.getMinLevel() != null ? String.valueOf(lc.getMinLevel()) : "?";
            String max = lc.getMaxLevel() != null ? String.valueOf(lc.getMaxLevel()) : "max";
            if (min.equals(max)) {
                return Component.translatable("cobblemonmarks.tooltip.condition.level.exact",
                        cyan(min)
                ).withStyle(s -> s.withColor(labelColor));
            }
            return Component.translatable("cobblemonmarks.tooltip.condition.level.range",
                    cyan(min),
                    Component.literal("-").withStyle(s -> s.withColor(labelColor)),
                    cyan(max)
            ).withStyle(s -> s.withColor(labelColor));
        }
        if (c instanceof DimensionCondition dc) {
            MutableComponent dims = dc.getRequiredDimensions().stream()
                    .map(d -> (MutableComponent) Component.literal(d).withStyle(s -> s.withColor(CYAN)))
                    .reduce((a, b) -> a.append(Component.literal(", ").withStyle(s -> s.withColor(labelColor))).append(b))
                    .orElse(Component.literal("?"));
            return Component.translatable("cobblemonmarks.tooltip.condition.dimension", dims)
                    .withStyle(s -> s.withColor(labelColor));
        }
        if (c instanceof StatusCondition sc) {
            MutableComponent statuses = sc.getRequiredStatuses().stream()
                    .map(st -> (MutableComponent) Component.literal(st).withStyle(s -> s.withColor(getStatusColorInt(st))))
                    .reduce((a, b) -> a.append(Component.literal(", ").withStyle(s -> s.withColor(labelColor))).append(b))
                    .orElse(Component.literal("?"));
            return Component.translatable("cobblemonmarks.tooltip.condition.status", statuses)
                    .withStyle(s -> s.withColor(labelColor));
        }
        if (c instanceof SizeCondition sc) {
            MutableComponent sizes = sc.getRequiredSizes().stream()
                    .map(sz -> (MutableComponent) Component.translatable("cobblemonmarks.size." + sz.name().toLowerCase())
                            .withStyle(s -> s.withColor(CYAN)))
                    .reduce((a, b) -> a.append(Component.literal("/").withStyle(s -> s.withColor(labelColor))).append(b))
                    .orElse(Component.literal("?"));
            return Component.translatable("cobblemonmarks.tooltip.condition.size", sizes)
                    .withStyle(s -> s.withColor(labelColor));
        }
        if (c instanceof FriendshipCondition fc)
            return Component.translatable("cobblemonmarks.tooltip.condition.friendship",
                    cyan(String.valueOf(fc.getRequiredFriendship()))
            ).withStyle(s -> s.withColor(labelColor));
        if (c instanceof ShinyCondition)
            return Component.translatable("cobblemonmarks.tooltip.condition.shiny")
                    .withStyle(s -> s.withColor(labelColor));
        if (c instanceof TimeOfBattleCondition tbc)
            return Component.translatable("cobblemonmarks.tooltip.condition.battle_turns",
                    cyan(String.valueOf(tbc.getRequiredCount()))
            ).withStyle(s -> s.withColor(labelColor));
        return Component.literal(c.getClass().getSimpleName()).withStyle(s -> s.withColor(labelColor));
    }

    private static String ticksToTime(int ticks) {
        int totalMinutes = (int) (((ticks + 6000) % 24000) / 24000.0 * 1440);
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return String.format("%02d:%02d", hours, minutes);
    }
}