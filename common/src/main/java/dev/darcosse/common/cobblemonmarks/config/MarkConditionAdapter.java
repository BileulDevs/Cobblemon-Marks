package dev.darcosse.common.cobblemonmarks.config;

import com.google.gson.*;
import dev.darcosse.common.cobblemonmarks.config.condition.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom GSON adapter responsible for the polymorphic (de)serialization of MarkConditions.
 * It maps specific JSON "type" keys to their corresponding Java subclasses and manages
 * the unique field requirements for each condition type.
 */
public class MarkConditionAdapter implements JsonDeserializer<MarkCondition>, JsonSerializer<MarkCondition> {

    /**
     * DESERIALIZATION: Converts a JSON element into a specific MarkCondition subclass.
     */
    @Override
    public MarkCondition deserialize(JsonElement json, Type typeOfT,
                                     JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        // Ensure the 'type' discriminator exists to determine which subclass to instantiate
        JsonElement typeElement = obj.get("type");
        if (typeElement == null || typeElement.isJsonNull()) {
            throw new JsonParseException("Missing 'type' field in condition: " + obj);
        }

        // Convert the type string to the ConditionType enum for switch-case logic
        ConditionType type = ConditionType.valueOf(typeElement.getAsString().toUpperCase());

        // Polymorphic instantiation based on the ConditionType
        return switch (type) {
            case FORM_KILL -> new FormKillCondition(
                    obj.get("requiredCount").getAsInt(),
                    jsonArrayToList(obj.getAsJsonArray("requiredForms")),
                    obj.get("nbtKey").getAsString()
            );
            case KILL -> new KillCondition(
                    obj.get("requiredKills").getAsInt(),
                    jsonArrayToList(obj.getAsJsonArray("requiredTypes")),
                    jsonArrayToList(obj.getAsJsonArray("requiredSpecies")),
                    obj.get("nbtKey").getAsString()
            );
            case FISHING_KILL -> new FishingKillCondition(
                    obj.get("requiredKills").getAsInt(),
                    jsonArrayToList(obj.getAsJsonArray("requiredTypes")),
                    jsonArrayToList(obj.getAsJsonArray("requiredSpecies")),
                    obj.get("nbtKey").getAsString()
            );
            case CATCH -> new CatchCondition(
                    obj.get("requiredCount").getAsInt(),
                    obj.get("nbtKey").getAsString()
            );
            case STREAK -> new StreakCondition(
                    obj.get("requiredStreak").getAsInt(),
                    obj.get("nbtKey").getAsString()
            );
            case WEATHER -> {
                // Deserialize an array of internal Weather enum values
                List<WeatherCondition.Weather> weathers = new ArrayList<>();
                for (JsonElement e : obj.getAsJsonArray("weathers")) {
                    weathers.add(WeatherCondition.Weather.valueOf(e.getAsString().toUpperCase()));
                }
                yield new WeatherCondition(weathers);
            }
            case BIOME -> new BiomeCondition(jsonArrayToList(obj.getAsJsonArray("biomes")));
            case TIME -> new TimeCondition(
                    obj.get("minTime").getAsLong(),
                    obj.get("maxTime").getAsLong()
            );
            case TIME_OF_BATTLE -> new TimeOfBattleCondition(
                    // Handle nullable/optional integer fields for turn counts
                    obj.has("minTurns") && !obj.get("minTurns").isJsonNull()
                            ? obj.get("minTurns").getAsInt() : null,
                    obj.has("maxTurns") && !obj.get("maxTurns").isJsonNull()
                            ? obj.get("maxTurns").getAsInt() : null
            );
            case LEVEL -> {
                boolean mustBeStronger = obj.has("mustBeStrongerThanKiller")
                        && obj.get("mustBeStrongerThanKiller").getAsBoolean();
                Integer minLevel = obj.has("minLevel") ? obj.get("minLevel").getAsInt() : null;
                Integer maxLevel = obj.has("maxLevel") ? obj.get("maxLevel").getAsInt() : null;
                yield new LevelCondition(minLevel, maxLevel, mustBeStronger);
            }
            case DIMENSION -> new DimensionCondition(jsonArrayToList(obj.getAsJsonArray("dimensions")));
            case STATUS -> new StatusCondition(jsonArrayToList(obj.getAsJsonArray("statuses")));
            case SIZE -> {
                // Deserialize internal Size enum values (e.g., SMALL, LARGE)
                List<SizeCondition.Size> sizes = new ArrayList<>();
                for (JsonElement e : obj.getAsJsonArray("sizes")) {
                    sizes.add(SizeCondition.Size.valueOf(e.getAsString().toUpperCase()));
                }
                boolean checkTarget = !obj.has("checkTarget") || obj.get("checkTarget").getAsBoolean();
                yield new SizeCondition(sizes, checkTarget);
            }
            case FRIENDSHIP -> new FriendshipCondition(obj.get("requiredFriendship").getAsInt());
            case DEATH -> new DeathCondition(
                    obj.get("requiredDeaths").getAsInt(),
                    obj.get("nbtKey").getAsString()
            );
            case SHINY -> new ShinyCondition();
        };
    }

    /**
     * SERIALIZATION: Converts a Java MarkCondition object into its JSON representation.
     */
    @Override
    public JsonElement serialize(MarkCondition src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        JsonObject obj = new JsonObject();

        // Perform manual type checking to populate the JSON with correct keys and values
        if (src instanceof FormKillCondition fkc) {
            obj.addProperty("type", "FORM_KILL");
            obj.addProperty("requiredCount", fkc.getRequiredCount());
            obj.add("requiredForms", listToJsonArray(fkc.getRequiredForms()));
            obj.addProperty("nbtKey", fkc.getNbtKey());

        } else if (src instanceof FishingKillCondition fkc) {
            obj.addProperty("type", "FISHING_KILL");
            obj.addProperty("requiredKills", fkc.getRequiredCount());
            obj.add("requiredTypes", listToJsonArray(fkc.getRequiredTypes()));
            obj.add("requiredSpecies", listToJsonArray(fkc.getRequiredSpecies()));
            obj.addProperty("nbtKey", fkc.getNbtKey());

        } else if (src instanceof CatchCondition cc) {
            obj.addProperty("type", "CATCH");
            obj.addProperty("requiredCount", cc.getRequiredCount());
            obj.addProperty("nbtKey", cc.getNbtKey());

        } else if (src instanceof StreakCondition sc) {
            obj.addProperty("type", "STREAK");
            obj.addProperty("requiredStreak", sc.getRequiredCount());
            obj.addProperty("nbtKey", sc.getNbtKey());

        } else if (src instanceof KillCondition kc) {
            obj.addProperty("type", "KILL");
            obj.addProperty("requiredKills", kc.getRequiredCount());
            obj.add("requiredTypes", listToJsonArray(kc.getRequiredTypes()));
            obj.add("requiredSpecies", listToJsonArray(kc.getRequiredSpecies()));
            obj.addProperty("nbtKey", kc.getNbtKey());
        } else if (src instanceof WeatherCondition wc) {
            obj.addProperty("type", "WEATHER");
            JsonArray arr = new JsonArray();
            for (WeatherCondition.Weather w : wc.getWeathers()) arr.add(w.name());
            obj.add("weathers", arr);
        } else if (src instanceof BiomeCondition bc) {
            obj.addProperty("type", "BIOME");
            obj.add("biomes", listToJsonArray(bc.getRequiredBiomes()));
        } else if (src instanceof TimeCondition tc) {
            obj.addProperty("type", "TIME");
            obj.addProperty("minTime", tc.getMinTime());
            obj.addProperty("maxTime", tc.getMaxTime());
        } else if (src instanceof TimeOfBattleCondition tbc) {
            obj.addProperty("type", "TIME_OF_BATTLE");
            if (tbc.getMinTurns() != null) obj.addProperty("minTurns", tbc.getMinTurns());
            if (tbc.getMaxTurns() != null) obj.addProperty("maxTurns", tbc.getMaxTurns());
        } else if (src instanceof LevelCondition lc) {
            obj.addProperty("type", "LEVEL");
            if (lc.getMinLevel() != null) obj.addProperty("minLevel", lc.getMinLevel());
            if (lc.getMaxLevel() != null) obj.addProperty("maxLevel", lc.getMaxLevel());
            obj.addProperty("mustBeStrongerThanKiller", lc.isMustBeStrongerThanKiller());
        } else if (src instanceof DimensionCondition dc) {
            obj.addProperty("type", "DIMENSION");
            obj.add("dimensions", listToJsonArray(dc.getRequiredDimensions()));
        } else if (src instanceof StatusCondition sc) {
            obj.addProperty("type", "STATUS");
            obj.add("statuses", listToJsonArray(sc.getRequiredStatuses()));
        } else if (src instanceof SizeCondition sc) {
            obj.addProperty("type", "SIZE");
            JsonArray arr = new JsonArray();
            for (SizeCondition.Size s : sc.getRequiredSizes()) arr.add(s.name());
            obj.add("sizes", arr);
            obj.addProperty("checkTarget", sc.isCheckTarget());
        } else if (src instanceof FriendshipCondition fc) {
            obj.addProperty("type", "FRIENDSHIP");
            obj.addProperty("requiredFriendship", fc.getRequiredFriendship());
        } else if (src instanceof DeathCondition dc) {
            obj.addProperty("type", "DEATH");
            obj.addProperty("requiredDeaths", dc.getRequiredCount());
            obj.addProperty("nbtKey", dc.getNbtKey());
        } else if (src instanceof ShinyCondition) {
            obj.addProperty("type", "SHINY");
        }

        return obj;
    }

    /** Helper to convert GSON JsonArrays into Java Lists of Strings. */
    private List<String> jsonArrayToList(JsonArray array) {
        List<String> list = new ArrayList<>();
        if (array != null) for (JsonElement e : array) list.add(e.getAsString());
        return list;
    }

    /** Helper to convert Java Lists of Strings into GSON JsonArrays. */
    private JsonArray listToJsonArray(List<String> list) {
        JsonArray array = new JsonArray();
        if (list != null) list.forEach(array::add);
        return array;
    }
}