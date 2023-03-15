package com.eteryun.modules.boss;

import com.eteryun.modules.IModule;
import com.eteryun.modules.boss.network.client.ClientboundPacketBoss;
import com.eteryun.network.PacketsProtocol;
import net.minecraft.network.protocol.PacketFlow;

public class BossModule implements IModule {
    @Override
    public String name() {
        return "boss";
    }

    @Override
    public void preInit() {
        PacketsProtocol.registerPacket(PacketFlow.CLIENTBOUND, ClientboundPacketBoss.class, ClientboundPacketBoss::new);
    }

    @Override
    public void init() {

    }

    @Override
    public void shutdown() {

    }
}
