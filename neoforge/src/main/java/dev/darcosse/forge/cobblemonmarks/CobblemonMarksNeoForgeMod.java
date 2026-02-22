package dev.darcosse.forge.cobblemonmarks;

import dev.darcosse.common.cobblemonmarks.CobblemonMarksMod;
import dev.darcosse.common.cobblemonmarks.network.PacketSender;
import dev.darcosse.forge.cobblemonmarks.network.NeoForgeNetworkHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * NeoForge entry point for Cobblemon Marks.
 * Manages event bus listeners and platform-specific service registration.
 */
@Mod(CobblemonMarksMod.MOD_ID)
public class CobblemonMarksNeoForgeMod {

    public CobblemonMarksNeoForgeMod(IEventBus modBus) {
        // Register setup lifecycle
        modBus.addListener(this::commonSetup);

        // Register network payloads
        modBus.addListener(NeoForgeNetworkHandler::register);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Initialize common mod logic
            CobblemonMarksMod.init();

            // Link common PacketSender to NeoForge's PacketDistributor
            PacketSender.setImpl(PacketDistributor::sendToPlayer);
        });
    }
}