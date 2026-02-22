package dev.darcosse.fabric.cobblemonmarks.client;

import dev.darcosse.fabric.cobblemonmarks.network.FabricNetworkHandler;
import net.fabricmc.api.ClientModInitializer;

/**
 * Fabric-specific client initialization.
 * Handles client-side network registration.
 */
public class CobblemonMarksFabricClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FabricNetworkHandler.registerClient();
    }
}