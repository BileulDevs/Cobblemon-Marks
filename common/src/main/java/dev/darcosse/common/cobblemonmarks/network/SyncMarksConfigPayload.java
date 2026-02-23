package dev.darcosse.common.cobblemonmarks.network;

import dev.darcosse.common.cobblemonmarks.config.MarksCondition;
import dev.darcosse.common.cobblemonmarks.config.MarksConfigLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Network payload for synchronizing the full list of mark conditions.
 * Ensures that client-side UI and logic match the server's configuration.
 */
public class SyncMarksConfigPayload implements CustomPacketPayload {

    /**
     * Unique identifier for this packet type.
     * Uses the "cobblemonmarks:sync_marks_config" resource location.
     */
    public static final Type<SyncMarksConfigPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("cobblemonmarks", "sync_marks_config")
    );

    /**
     * The list of conditions being synchronized.
     */
    public final List<MarksCondition> conditions;

    public SyncMarksConfigPayload(List<MarksCondition> conditions) {
        this.conditions = conditions;
    }

    /**
     * DECODING: Reads the incoming byte buffer on the receiver side.
     * Converts the UTF string (JSON) back into a List of MarksCondition objects.
     */
    public static SyncMarksConfigPayload decode(FriendlyByteBuf buf) {
        String json = buf.readUtf();
        return new SyncMarksConfigPayload(MarksConfigLoader.fromJson(json));
    }

    /**
     * ENCODING: Prepares the packet for transmission over the network.
     * Serializes the List of MarksCondition objects into a JSON string.
     */
    public static void encode(SyncMarksConfigPayload payload, FriendlyByteBuf buf) {
        buf.writeUtf(MarksConfigLoader.toJson(payload.conditions));
    }

    /**
     * Returns the unique payload type identifier required by the Minecraft networking API.
     */
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}