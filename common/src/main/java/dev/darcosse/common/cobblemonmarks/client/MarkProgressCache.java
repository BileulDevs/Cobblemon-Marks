package dev.darcosse.common.cobblemonmarks.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side cache that stores the progression of various Mark conditions for Pokémon.
 * This allows the UI to display up-to-date progress in tooltips without constant NBT polling.
 *
 * @author Darcosse
 * @version 1.0
 * @since 2026
 */
public class MarkProgressCache {

    /**
     * Thread-safe map storing progress values (keyed by NBT strings) for each Pokémon UUID.
     */
    private static final Map<UUID, Map<String, Integer>> CACHE = new ConcurrentHashMap<>();

    /**
     * Updates the cache for a specific Pokémon with a new map of progress data.
     */
    public static void update(UUID pokemonUUID, Map<String, Integer> progressMap) {
        CACHE.put(pokemonUUID, new HashMap<>(progressMap));
    }

    /**
     * Retrieves the current progress value for a specific condition key.
     * Returns 0 if no data is found for the Pokémon or the key.
     */
    public static int get(UUID pokemonUUID, String nbtKey) {
        Map<String, Integer> map = CACHE.get(pokemonUUID);
        if (map == null) return 0;
        return map.getOrDefault(nbtKey, 0);
    }

    /**
     * Clears all cached progress data, typically used when changing servers or logging out.
     */
    public static void clear() {
        CACHE.clear();
    }
}