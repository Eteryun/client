package com.eteryun;

import com.eteryun.event.EventTarget;
import com.eteryun.event.impl.input.KeyInputEvent;
import com.eteryun.network.PacketsProtocol;
import com.eteryun.network.server.ServerboundPacketPlayerAction;
import net.minecraft.client.Minecraft;


public class Events {
    private Minecraft mc = Minecraft.getInstance();

    @EventTarget
    public void onKeyEvent(KeyInputEvent event) {
        if (event.getAction() == 0) {
            if (Eteryun.keyBackTool.consumeClick() && !mc.player.isSpectator()) {
                PacketsProtocol.sendPacket(new ServerboundPacketPlayerAction(mc.player.getUUID(), ServerboundPacketPlayerAction.Action.SWAP_BACKTOOL));
            }
        }
    }
}
