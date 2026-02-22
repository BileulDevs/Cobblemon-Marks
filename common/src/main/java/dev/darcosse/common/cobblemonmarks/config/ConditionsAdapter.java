package dev.darcosse.common.cobblemonmarks.config;

import com.google.gson.*;
import dev.darcosse.common.cobblemonmarks.config.condition.KillCondition;
import dev.darcosse.common.cobblemonmarks.config.condition.MarkCondition;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * GSON adapter for the Conditions container class.
 * Handles the mapping between the JSON structure and the Conditions object.
 */
public class ConditionsAdapter implements JsonDeserializer<Conditions>, JsonSerializer<Conditions> {

    /**
     * DESERIALIZATION: Converts JSON data into a Conditions Java object.
     */
    @Override
    public Conditions deserialize(JsonElement json, Type typeOfT,
                                  JsonDeserializationContext context) throws JsonParseException {
        // Convert generic JsonElement to a JsonObject to access specific keys
        JsonObject obj = json.getAsJsonObject();

        KillCondition killCondition = null;
        // Logic to safely extract the main 'condition' (Kill/Streak/Catch)
        if (obj.has("condition")
                && !obj.get("condition").isJsonNull()
                && obj.get("condition").isJsonObject()
                && obj.getAsJsonObject("condition").size() > 0) {

            // Delegate the polymorphic deserialization to the MarkCondition class type
            killCondition = context.deserialize(obj.get("condition"), MarkCondition.class);
        }

        // Parse the 'required' conditions list
        List<MarkCondition> required = new ArrayList<>();
        if (obj.has("required")) {
            for (JsonElement e : obj.getAsJsonArray("required")) {
                // Deserialize each individual condition in the array
                required.add(context.deserialize(e, MarkCondition.class));
            }
        }

        // Parse the 'excluded' conditions list
        List<MarkCondition> excluded = new ArrayList<>();
        if (obj.has("excluded")) {
            for (JsonElement e : obj.getAsJsonArray("excluded")) {
                // Deserialize each individual condition in the array
                excluded.add(context.deserialize(e, MarkCondition.class));
            }
        }

        // Construct and return the final Conditions object
        return new Conditions(killCondition, required, excluded);
    }

    /**
     * SERIALIZATION: Converts a Conditions Java object back into a JSON structure.
     */
    @Override
    public JsonElement serialize(Conditions src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        JsonObject obj = new JsonObject();

        // Serialize the primary KillCondition
        if (src.getKillCondition() != null) {
            obj.add("condition", context.serialize(src.getKillCondition(), MarkCondition.class));
        } else {
            // Explicitly add null to the JSON if no condition exists
            obj.add("condition", JsonNull.INSTANCE);
        }

        // Construct the 'required' JSON array
        JsonArray required = new JsonArray();
        for (MarkCondition c : src.getRequired()) {
            required.add(context.serialize(c, MarkCondition.class));
        }
        obj.add("required", required);

        // Construct the 'excluded' JSON array
        JsonArray excluded = new JsonArray();
        for (MarkCondition c : src.getExcluded()) {
            excluded.add(context.serialize(c, MarkCondition.class));
        }
        obj.add("excluded", excluded);

        return obj;
    }
}