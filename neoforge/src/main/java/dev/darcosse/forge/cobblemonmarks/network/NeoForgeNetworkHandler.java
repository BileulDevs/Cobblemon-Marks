package dev.darcosse.forge.cobblemonmarks.network;

import dev.darcosse.common.cobblemonmarks.client.MarkProgressCache;
import dev.darcosse.common.cobblemonmarks.config.MarksConfig;
import dev.darcosse.common.cobblemonmarks.network.SyncMarkProgressPayload;
import dev.darcosse.common.cobblemonmarks.network.SyncMarksConfigPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Handles network registration for the NeoForge platform.
 * Utilizes the Registrar pattern to define S2C (Server-to-Client) communication.
 */
public class NeoForgeNetworkHandler {

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                SyncMarkProgressPayload.TYPE,
                StreamCodec.of(
                        (buf, payload) -> SyncMarkProgressPayload.encode(payload, (FriendlyByteBuf) buf),
                        buf -> SyncMarkProgressPayload.decode((FriendlyByteBuf) buf)
                ),
                (payload, context) -> context.enqueueWork(() ->
                        // Executes on the main client thread to update the cache safely
                        MarkProgressCache.update(payload.pokemonUUID, payload.progressMap)
                )
        );

        registrar.playToClient(
                SyncMarksConfigPayload.TYPE,
                StreamCodec.of(
                        (buf, payload) -> SyncMarksConfigPayload.encode(payload, (FriendlyByteBuf) buf),
                        buf -> SyncMarksConfigPayload.decode((FriendlyByteBuf) buf)
                ),
                (payload, context) -> context.enqueueWork(() ->
                        MarksConfig.CONDITIONS = payload.conditions
                )
        );
    }
}