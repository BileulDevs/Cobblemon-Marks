package dev.darcosse.common.cobblemonmarks.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Network payload used to synchronize Mark progression for a specific Pokémon.
 * This packet transfers a map of NBT keys and their current counter values
 * to update the client-side UI (tooltips and progress bars).
 *
 * @author Darcosse
 * @version 1.0
 * @since 2026
 */
public class SyncMarkProgressPayload implements CustomPacketPayload {

    /** Unique identifier for the packet type. */
    public static final Type<SyncMarkProgressPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("cobblemonmarks", "sync_mark_progress")
    );

    /** The UUID of the Pokémon whose progress is being updated. */
    public final UUID pokemonUUID;

    /** A map where the key is the NBT progress tag and the value is the current count. */
    public final Map<String, Integer> progressMap;

    /**
     * Constructs a new synchronization payload.
     *
     * @param pokemonUUID The unique ID of the target Pokémon.
     * @param progressMap The collection of NBT keys and progress values.
     */
    public SyncMarkProgressPayload(UUID pokemonUUID, Map<String, Integer> progressMap) {
        this.pokemonUUID = pokemonUUID;
        this.progressMap = progressMap;
    }

    /**
     * Decodes the payload from the network buffer.
     *
     * @param buf The incoming byte buffer.
     * @return A new instance of SyncMarkProgressPayload.
     */
    public static SyncMarkProgressPayload decode(FriendlyByteBuf buf) {
        UUID uuid = buf.readUUID();
        int size = buf.readVarInt();
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            map.put(buf.readUtf(), buf.readVarInt());
        }
        return new SyncMarkProgressPayload(uuid, map);
    }

    /**
     * Encodes the payload into the network buffer for transmission.
     *
     * @param payload The payload instance to encode.
     * @param buf     The outgoing byte buffer.
     */
    public static void encode(SyncMarkProgressPayload payload, FriendlyByteBuf buf) {
        buf.writeUUID(payload.pokemonUUID);
        buf.writeVarInt(payload.progressMap.size());
        for (var entry : payload.progressMap.entrySet()) {
            buf.writeUtf(entry.getKey());
            buf.writeVarInt(entry.getValue());
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}