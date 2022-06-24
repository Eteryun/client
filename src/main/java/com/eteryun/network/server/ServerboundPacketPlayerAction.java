package com.eteryun.network.server;

import com.eteryun.network.Packet;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class ServerboundPacketPlayerAction implements Packet {
    private final UUID uuid;
    private final Action action;

    public ServerboundPacketPlayerAction(UUID uuid, Action action) {
        this.uuid = uuid;
        this.action = action;
    }

    public ServerboundPacketPlayerAction(FriendlyByteBuf buffer) {
        this.uuid = buffer.readUUID();
        this.action = buffer.readEnum(Action.class);
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeUUID(this.uuid);
        pBuffer.writeEnum(this.action);
    }

    @Override
    public void handle() {
        // nope
    }

    public static enum Action {
        SWAP_BACKTOOL
    }
}
