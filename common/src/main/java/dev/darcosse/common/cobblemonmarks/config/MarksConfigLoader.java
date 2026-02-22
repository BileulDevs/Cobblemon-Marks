package dev.darcosse.common.cobblemonmarks.config;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import dev.darcosse.common.cobblemonmarks.CobblemonMarksMod;
import dev.darcosse.common.cobblemonmarks.config.condition.MarkCondition;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static dev.darcosse.common.cobblemonmarks.config.MarksConfig.DEFAULT_CONDITIONS;

/**
 * Utility class responsible for reading and writing the 'conditions.json' configuration file.
 * It handles file persistence, JSON parsing via GSON, and data integrity checks.
 */
public class MarksConfigLoader {

    /**
     * Pre-configured GSON instance.
     * Includes custom Type Adapters to handle the complex, polymorphic nature
     * of MarkConditions and their sub-requirements.
     */
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(MarkCondition.class, new MarkConditionAdapter())
            .registerTypeAdapter(Conditions.class, new ConditionsAdapter())
            .setPrettyPrinting() // Makes the JSON file human-readable
            .create();

    /**
     * Loads the list of MarkConditions from the disk.
     * * @param configDir The root directory provided by the mod loader (Fabric/NeoForge).
     * @return A list of unique MarkConditions found in the file, or defaults if loading fails.
     */
    public static List<MarksCondition> load(Path configDir) {
        // Resolve the specific file path: config/cobblemonmarks/conditions.json
        Path folder = configDir.resolve(CobblemonMarksMod.MOD_ID);
        Path file   = folder.resolve("conditions.json");

        // FIRST BOOT LOGIC: If the file doesn't exist, create it using hardcoded defaults
        if (!Files.exists(file)) {
            CobblemonMarksMod.LOGGER.info("conditions.json absent, generating default file...");
            save(DEFAULT_CONDITIONS, configDir);
            return DEFAULT_CONDITIONS;
        }

        try (Reader reader = Files.newBufferedReader(file)) {
            // Define the generic type for the List to assist GSON's reflection
            Type listType = new TypeToken<List<MarksCondition>>(){}.getType();
            List<MarksCondition> loaded = GSON.fromJson(reader, listType);

            // VALIDATION: If the file exists but is empty, fallback to defaults
            if (loaded == null || loaded.isEmpty()) {
                CobblemonMarksMod.LOGGER.warn("conditions.json is empty, using default values");
                return DEFAULT_CONDITIONS;
            }

            /* * DEDUPLICATION LOGIC:
             * Ensures that if a user accidentally pastes the same Mark ID twice
             * in the JSON, the mod only processes it once to prevent event conflicts.
             */
            List<MarksCondition> deduplicated = new ArrayList<>();
            Set<String> seen = new LinkedHashSet<>(); // LinkedHashSet preserves the file's order
            for (MarksCondition mc : loaded) {
                if (seen.add(mc.getMarkIdentifier())) {
                    deduplicated.add(mc);
                } else {
                    CobblemonMarksMod.LOGGER.warn("Ignored duplicate Mark ID: {}", mc.getMarkIdentifier());
                }
            }

            CobblemonMarksMod.LOGGER.info("Successfully loaded conditions.json ({} entries)", deduplicated.size());
            return deduplicated;
        } catch (IOException e) {
            // CATCH-ALL: Returns defaults if the JSON is corrupted or the file is locked
            CobblemonMarksMod.LOGGER.error("Error reading conditions.json", e);
            return DEFAULT_CONDITIONS;
        }
    }

    /**
     * Writes the current list of conditions to the config file.
     * * @param conditions The list of conditions to save.
     * @param configDir The root config directory.
     */
    public static void save(List<MarksCondition> conditions, Path configDir) {
        Path folder = configDir.resolve(CobblemonMarksMod.MOD_ID);
        Path file   = folder.resolve("conditions.json");

        try {
            // Ensure the sub-folder (cobblemonmarks/) exists before writing
            Files.createDirectories(folder);
            try (Writer writer = Files.newBufferedWriter(file)) {
                Type listType = new TypeToken<List<MarksCondition>>(){}.getType();
                GSON.toJson(conditions, listType, writer);
                CobblemonMarksMod.LOGGER.info("Generated conditions.json at {}", file);
            }
        } catch (IOException e) {
            CobblemonMarksMod.LOGGER.error("Error writing conditions.json", e);
        }
    }
}