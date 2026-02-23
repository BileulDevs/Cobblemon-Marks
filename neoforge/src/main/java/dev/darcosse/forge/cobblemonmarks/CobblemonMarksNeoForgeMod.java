package dev.darcosse.forge.cobblemonmarks;

import dev.darcosse.common.cobblemonmarks.CobblemonMarksMod;
import dev.darcosse.common.cobblemonmarks.config.MarksConfig;
import dev.darcosse.common.cobblemonmarks.network.PacketSender;
import dev.darcosse.common.cobblemonmarks.network.SyncMarksConfigPayload;
import dev.darcosse.forge.cobblemonmarks.network.NeoForgeNetworkHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * NeoForge entry point for Cobblemon Marks.
 * This class initializes the mod on the NeoForge platform, setting up
 * configuration paths, networking implementations, and global event listeners.
 */
@Mod(CobblemonMarksMod.MOD_ID)
public class CobblemonMarksNeoForgeMod {

    public CobblemonMarksNeoForgeMod(IEventBus modBus) {
        CobblemonMarksMod.loadConfig(FMLPaths.CONFIGDIR.get());
        CobblemonMarksMod.init();
        modBus.addListener(NeoForgeNetworkHandler::register);
        PacketSender.setImpl(PacketDistributor::sendToPlayer);
        NeoForge.EVENT_BUS.addListener(this::onPlayerJoin);
    }

    /**
     * Event triggered when a player logs into the server.
     * Used here to sync the server's Mark configuration with the client.
     */
    private void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PacketSender.sendToPlayer(player, new SyncMarksConfigPayload(MarksConfig.CONDITIONS));
        }
    }
}