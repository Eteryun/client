package com.eteryun.network;

import net.minecraft.network.FriendlyByteBuf;

public interface Packet {
    void write(FriendlyByteBuf pBuffer);

    void handle();
}
