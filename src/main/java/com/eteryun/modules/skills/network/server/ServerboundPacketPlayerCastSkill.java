package com.eteryun.modules.skills.network.server;

import com.eteryun.network.Packet;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class ServerboundPacketPlayerCastSkill implements Packet {
    private UUID uuid;
    int slot;

    public ServerboundPacketPlayerCastSkill(UUID uuid, int slot) {
        this.uuid = uuid;
        this.slot = slot;
    }

    public ServerboundPacketPlayerCastSkill(FriendlyByteBuf buffer) {
        this.uuid = buffer.readUUID();
        this.slot = buffer.readInt();
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeUUID(uuid);
        pBuffer.writeInt(slot);
    }

    @Override
    public void handle() {

    }
}
