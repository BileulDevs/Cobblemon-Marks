package dev.darcosse.fabric.cobblemonmarks.network;

import dev.darcosse.common.cobblemonmarks.client.MarkProgressCache;
import dev.darcosse.common.cobblemonmarks.network.SyncMarkProgressPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class FabricNetworkHandler {

    public static void registerServer() {
        PayloadTypeRegistry.playS2C().register(
                SyncMarkProgressPayload.TYPE,
                StreamCodec.of(
                        (buf, payload) -> SyncMarkProgressPayload.encode(payload, (FriendlyByteBuf) buf),
                        buf -> SyncMarkProgressPayload.decode((FriendlyByteBuf) buf)
                )
        );
    }

    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(SyncMarkProgressPayload.TYPE, (payload, context) -> {
            context.client().execute(() ->
                    MarkProgressCache.update(payload.pokemonUUID, payload.progressMap)
            );
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
                MarkProgressCache.clear()
        );
    }
}