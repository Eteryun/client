package com.eteryun.ui.events.impl;

import com.eteryun.screens.ConnectingScreen;
import com.eteryun.screens.EmptyScreen;
import com.eteryun.ui.events.UIEventTarget;
import com.google.gson.JsonObject;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.resolver.ServerAddress;

public class DefaultEvents {
    private static Minecraft minecraft = Minecraft.getInstance();

    @UIEventTarget(name = "connect/start")
    public void connectStart(JsonObject object) {
        Screen screen = minecraft.screen;
        if (!(screen instanceof EmptyScreen)) {
            screen = new EmptyScreen("titleScreen");
            ((EmptyScreen) screen).loadUrl("http://127.0.0.1:5500/index.html");
        }
        ConnectingScreen.startConnecting(screen, minecraft, ServerAddress.parseString("localhost"), null);
    }

    @UIEventTarget(name = "connect/abort")
    public void connectAbort(JsonObject object) {
        Screen screen = minecraft.screen;
        if (screen instanceof ConnectingScreen) {
            ((ConnectingScreen) screen).abort();
        }
    }

    @UIEventTarget(name = "quit")
    public void quit(JsonObject object) {
        minecraft.stop();
    }

    @UIEventTarget(name = "open/link")
    public void openLink(String link) {
        Util.getPlatform().openUri(link);
    }
}
