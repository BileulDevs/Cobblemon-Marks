package dev.darcosse.common.cobblemonmarks.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

/**
 * Static utility for dispatching custom network packets to players.
 * This class uses an implementation-agnostic pattern to remain compatible
 * across different mod loaders (Fabric/NeoForge).
 *
 * @author Darcosse
 * @version 1.0
 * @since 2026
 */
public class PacketSender {

    /** The active network implementation, assigned during mod initialization. */
    private static Sender IMPL;

    /**
     * Assigns the platform-specific implementation of the packet sender.
     * * @param impl The implementation provided by the mod loader's initialization.
     */
    public static void setImpl(Sender impl) {
        IMPL = impl;
    }

    /**
     * Sends a custom payload to a specific server player.
     *
     * @param player  The recipient of the packet.
     * @param payload The custom data to be sent (e.g., SyncMarkProgressPayload).
     */
    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        if (IMPL != null) {
            IMPL.send(player, payload);
        }
    }

    /**
     * Bridge interface between the common code and platform-specific networking APIs.
     */
    public interface Sender {
        /**
         * Dispatches a payload via the platform's networking channel.
         * * @param player  The target player.
         * @param payload The data payload.
         */
        void send(ServerPlayer player, CustomPacketPayload payload);
    }
}