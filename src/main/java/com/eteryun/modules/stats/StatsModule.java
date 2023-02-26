package com.eteryun.modules.stats;

import com.eteryun.modules.IModule;
import com.eteryun.modules.stats.network.client.ClientboundPacketPlayerStats;
import com.eteryun.network.PacketsProtocol;
import net.minecraft.network.protocol.PacketFlow;

public class StatsModule implements IModule {
    @Override
    public String name() {
        return "stats";
    }

    @Override
    public void preInit() {
        PacketsProtocol.registerPacket(PacketFlow.CLIENTBOUND, ClientboundPacketPlayerStats.class, ClientboundPacketPlayerStats::new);
    }

    @Override
    public void init() {

    }

    @Override
    public void shutdown() {

    }
}
