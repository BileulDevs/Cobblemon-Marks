package dev.darcosse.fabric.cobblemonmarks.client;

import dev.darcosse.fabric.cobblemonmarks.network.FabricNetworkHandler;
import net.fabricmc.api.ClientModInitializer;

public class CobblemonMarksFabricClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FabricNetworkHandler.registerClient();
    }
}