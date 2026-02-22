package dev.darcosse.common.cobblemonmarks;

import dev.darcosse.common.cobblemonmarks.handler.MarksHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common entry point for the Cobblemon Marks mod.
 * Responsible for global initialization and registering core handlers
 * regardless of the platform (Fabric or Forge).
 *
 * @author Darcosse
 * @version 1.0
 * @since 2026
 */
public class CobblemonMarksMod {

    /**
     * Unique identifier for the mod.
     */
    public static final String MOD_ID = "cobblemonmarks";

    /**
     * Global logger instance for mod synchronization and debugging.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger("Cobblemon Marks");

    /**
     * Initializes the mod components and triggers the registration
     * of the MarksHandler.
     */
    public static void init() {
        LOGGER.info("Cobblemon Marks initialized");
        MarksHandler.register();
    }
}