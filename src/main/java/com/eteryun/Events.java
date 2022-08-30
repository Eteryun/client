package com.eteryun;

import com.eteryun.event.EventTarget;
import com.eteryun.event.impl.input.KeyInputEvent;
import com.eteryun.event.impl.screen.GuiOpenEvent;
import com.eteryun.network.PacketsProtocol;
import com.eteryun.network.server.ServerboundPacketPlayerAction;
import com.eteryun.screens.EmptyScreen;
import com.ramon.ultralight.UltralightResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;


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

    @EventTarget
    public void onScreenOpen(GuiOpenEvent event) {
        Screen screen = event.getGui();

        if (screen instanceof TitleScreen || screen instanceof JoinMultiplayerScreen) {
            EmptyScreen emptyScreen = new EmptyScreen("titleScreen");
            emptyScreen.loadUrl(UltralightResources.getNUI("screens", "index.html"));
            event.setGui(emptyScreen);
        }

        if (screen instanceof PauseScreen) {
            EmptyScreen emptyScreen = new EmptyScreen("pauseScreen");
            emptyScreen.loadUrl(UltralightResources.getNUI("screens", "index.html", "pause"));
            event.setGui(emptyScreen);
        }

        if (screen instanceof ReceivingLevelScreen) {
            EmptyScreen emptyScreen = new EmptyScreen("receivingLevel");
            emptyScreen.loadUrl(UltralightResources.getNUI("screens", "index.html", "receiving"));
            event.setGui(emptyScreen);
        }
    }
}
