package dev.darcosse.common.cobblemonmarks.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public class PacketSender {

    private static Sender IMPL;

    public static void setImpl(Sender impl) {
        IMPL = impl;
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        if (IMPL != null) IMPL.send(player, payload);
    }

    public interface Sender {
        void send(ServerPlayer player, CustomPacketPayload payload);
    }
}