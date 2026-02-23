package dev.darcosse.fabric.cobblemonmarks.network;

import dev.darcosse.common.cobblemonmarks.client.MarkProgressCache;
import dev.darcosse.common.cobblemonmarks.config.MarksConfig;
import dev.darcosse.common.cobblemonmarks.network.SyncMarkProgressPayload;
import dev.darcosse.common.cobblemonmarks.network.SyncMarksConfigPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * Handles the registration of network payloads and receivers for the Fabric platform.
 */
public class FabricNetworkHandler {

    /**
     * Registers the SyncMarkProgressPayload for Server-to-Client communication.
     */
    public static void registerServer() {
        PayloadTypeRegistry.playS2C().register(
                SyncMarkProgressPayload.TYPE,
                StreamCodec.of(
                        (buf, payload) -> SyncMarkProgressPayload.encode(payload, (FriendlyByteBuf) buf),
                        buf -> SyncMarkProgressPayload.decode((FriendlyByteBuf) buf)
                )
        );

        PayloadTypeRegistry.playS2C().register(
                SyncMarksConfigPayload.TYPE,
                StreamCodec.of(
                        (buf, payload) -> SyncMarksConfigPayload.encode(payload, (FriendlyByteBuf) buf),
                        buf -> SyncMarksConfigPayload.decode((FriendlyByteBuf) buf)
                )
        );

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            sender.sendPacket(new SyncMarksConfigPayload(MarksConfig.CONDITIONS));
        });
    }

    /**
     * Registers the client-side receiver and handles cache cleanup on disconnect.
     */
    public static void registerClient() {
        // Handle incoming sync packets from the server
        ClientPlayNetworking.registerGlobalReceiver(SyncMarkProgressPayload.TYPE, (payload, context) -> {
            context.client().execute(() ->
                    MarkProgressCache.update(payload.pokemonUUID, payload.progressMap)
            );
        });

        // Ensure the cache is cleared when leaving a server to prevent data ghosting
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
                MarkProgressCache.clear()
        );

        ClientPlayNetworking.registerGlobalReceiver(SyncMarksConfigPayload.TYPE, (payload, context) -> {
            context.client().execute(() ->
                    MarksConfig.CONDITIONS = payload.conditions
            );
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            MarkProgressCache.clear();
            MarksConfig.CONDITIONS = MarksConfig.DEFAULT_CONDITIONS;
        });
    }
}