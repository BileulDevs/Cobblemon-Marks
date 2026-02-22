package dev.darcosse.fabric.cobblemonmarks;

import dev.darcosse.common.cobblemonmarks.CobblemonMarksMod;
import dev.darcosse.common.cobblemonmarks.network.PacketSender;
import dev.darcosse.fabric.cobblemonmarks.network.FabricNetworkHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.api.ModInitializer;

public class CobblemonMarksFabricMod implements ModInitializer {

    @Override
    public void onInitialize() {
        CobblemonMarksMod.init();
        FabricNetworkHandler.registerServer();
        PacketSender.setImpl((player, payload) ->
                ServerPlayNetworking.send(player, payload)
        );
    }
}