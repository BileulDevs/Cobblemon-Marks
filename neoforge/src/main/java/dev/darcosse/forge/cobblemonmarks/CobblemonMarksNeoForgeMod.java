package dev.darcosse.forge.cobblemonmarks;

import dev.darcosse.common.cobblemonmarks.CobblemonMarksMod;
import dev.darcosse.common.cobblemonmarks.network.PacketSender;
import dev.darcosse.forge.cobblemonmarks.network.NeoForgeNetworkHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@Mod(CobblemonMarksMod.MOD_ID)
public class CobblemonMarksNeoForgeMod {

    public CobblemonMarksNeoForgeMod(IEventBus modBus) {
        modBus.addListener(this::commonSetup);
        modBus.addListener(NeoForgeNetworkHandler::register);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(CobblemonMarksMod::init);
        PacketSender.setImpl(PacketDistributor::sendToPlayer);
    }
}