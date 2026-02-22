package dev.darcosse.common.cobblemonmarks;

import dev.darcosse.common.cobblemonmarks.config.MarksConfig;
import dev.darcosse.common.cobblemonmarks.config.MarksConfigLoader;
import dev.darcosse.common.cobblemonmarks.handler.MarksHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * Common entry point for the Cobblemon Marks mod.
 * This class handles the global initialization of the mod, including
 * configuration loading and event handler registration, ensuring
 * consistency across all supported platforms.
 *
 * @author Darcosse
 * @version 1.1
 * @since 2026
 */
public class CobblemonMarksMod {

    /**
     * Unique identifier used for networking, NBT keys, and resource lookups.
     */
    public static final String MOD_ID = "cobblemonmarks";

    /**
     * Standard SLF4J logger for logging mod status and debugging information.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger("Cobblemon Marks");

    /**
     * Core initialization method called by platform-specific mod loaders.
     * It registers the MarksHandler to start listening for Cobblemon events.
     */
    public static void init() {
        LOGGER.info("Initializing Cobblemon Marks core components...");
        MarksHandler.register();
    }

    /**
     * Loads the mod configuration from the file system.
     * This fills the static list of MarksConditions used throughout the mod.
     *
     * @param configDir The path to the directory where the configuration JSON is stored.
     */
    public static void loadConfig(Path configDir) {
        LOGGER.info("Loading marks configurations from: {}", configDir);
        MarksConfig.CONDITIONS = MarksConfigLoader.load(configDir);
        LOGGER.info("Successfully loaded {} mark conditions.", MarksConfig.CONDITIONS.size());
    }
}