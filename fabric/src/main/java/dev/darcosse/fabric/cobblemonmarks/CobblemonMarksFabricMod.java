package dev.darcosse.fabric.cobblemonmarks;

import dev.darcosse.common.cobblemonmarks.CobblemonMarksMod;
import dev.darcosse.common.cobblemonmarks.network.PacketSender;
import dev.darcosse.fabric.cobblemonmarks.network.FabricNetworkHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Fabric-specific common initialization.
 * Links the platform-agnostic PacketSender to Fabric's ServerPlayNetworking.
 */
public class CobblemonMarksFabricMod implements ModInitializer {

    @Override
    public void onInitialize() {
        CobblemonMarksMod.loadConfig(FabricLoader.getInstance().getConfigDir());
        CobblemonMarksMod.init();
        FabricNetworkHandler.registerServer();
        PacketSender.setImpl(ServerPlayNetworking::send);
    }
}