package dev.darcosse.common.cobblemonmarks;

import dev.darcosse.common.cobblemonmarks.handler.MarksHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CobblemonMarksMod {

    public static final String MOD_ID = "cobblemonmarks";
    public static final Logger LOGGER = LoggerFactory.getLogger("Cobblemon Marks");

    public static void init() {
        LOGGER.info("Cobblemon Marks initialized");
        MarksHandler.register();
    }
}