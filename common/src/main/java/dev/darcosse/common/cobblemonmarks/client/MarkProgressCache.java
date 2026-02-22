package dev.darcosse.common.cobblemonmarks.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MarkProgressCache {

    private static final Map<UUID, Map<String, Integer>> CACHE = new ConcurrentHashMap<>();

    public static void update(UUID pokemonUUID, Map<String, Integer> progressMap) {
        CACHE.put(pokemonUUID, new HashMap<>(progressMap));
    }

    public static int get(UUID pokemonUUID, String nbtKey) {
        Map<String, Integer> map = CACHE.get(pokemonUUID);
        if (map == null) return 0;
        return map.getOrDefault(nbtKey, 0);
    }

    public static void clear() {
        CACHE.clear();
    }
}