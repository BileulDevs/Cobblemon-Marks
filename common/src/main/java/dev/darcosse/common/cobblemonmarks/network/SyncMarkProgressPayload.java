package dev.darcosse.common.cobblemonmarks.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SyncMarkProgressPayload implements CustomPacketPayload {

    public static final Type<SyncMarkProgressPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("cobblemonmarks", "sync_mark_progress")
    );

    public final UUID pokemonUUID;
    public final Map<String, Integer> progressMap;

    public SyncMarkProgressPayload(UUID pokemonUUID, Map<String, Integer> progressMap) {
        this.pokemonUUID = pokemonUUID;
        this.progressMap = progressMap;
    }

    public static SyncMarkProgressPayload decode(FriendlyByteBuf buf) {
        UUID uuid = buf.readUUID();
        int size = buf.readVarInt();
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            map.put(buf.readUtf(), buf.readVarInt());
        }
        return new SyncMarkProgressPayload(uuid, map);
    }

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