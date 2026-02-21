package dev.darcosse.fabric.cobblemonmarks;

import dev.darcosse.common.cobblemonmarks.config.MarksConfig;
import dev.darcosse.common.cobblemonmarks.handler.MarksHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class CobblemonMarksFabricMod implements ModInitializer {

    @Override
    public void onInitialize() {
        MarksHandler.register();
    }
}