package com.eteryun.mixin.network;

import com.eteryun.network.Packet;
import com.eteryun.network.PacketsProtocol;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.eteryun.utils.Constants.ETERYUN_BRAND;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void handleCustomPayload(ClientboundCustomPayloadPacket pPacket, CallbackInfo ci) {
        if (pPacket.getIdentifier().equals(ETERYUN_BRAND)) {
            ci.cancel();

            FriendlyByteBuf byteBuf = pPacket.getData();
            int id = byteBuf.readInt();
            Packet packet = PacketsProtocol.createPacket(PacketFlow.CLIENTBOUND, id, byteBuf);
            if (packet != null) {
                packet.handle();
            }
        }
    }
}
