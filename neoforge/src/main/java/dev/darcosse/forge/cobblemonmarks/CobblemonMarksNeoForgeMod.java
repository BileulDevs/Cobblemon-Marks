package dev.darcosse.forge.cobblemonmarks;

import dev.darcosse.common.cobblemonmarks.CobblemonMarksMod;
import dev.darcosse.common.cobblemonmarks.config.MarksConfig;
import dev.darcosse.common.cobblemonmarks.handler.MarksHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;

@Mod(CobblemonMarksMod.MOD_ID)
public class CobblemonMarksNeoForgeMod {

    public CobblemonMarksNeoForgeMod(IEventBus modBus) {
        modBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(MarksHandler::register);
    }
}