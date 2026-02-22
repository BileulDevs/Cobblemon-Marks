package dev.darcosse.fabric.cobblemonmarks;

import dev.darcosse.common.cobblemonmarks.CobblemonMarksMod;
import dev.darcosse.common.cobblemonmarks.network.PacketSender;
import dev.darcosse.fabric.cobblemonmarks.network.FabricNetworkHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.api.ModInitializer;

/**
 * Fabric-specific common initialization.
 * Links the platform-agnostic PacketSender to Fabric's ServerPlayNetworking.
 */
public class CobblemonMarksFabricMod implements ModInitializer {

    @Override
    public void onInitialize() {
        // Initialize common logic (Handlers, Config, etc.)
        CobblemonMarksMod.init();

        // Register S2C payloads
        FabricNetworkHandler.registerServer();

        // Inject Fabric implementation into the common PacketSender
        PacketSender.setImpl(ServerPlayNetworking::send);
    }
}