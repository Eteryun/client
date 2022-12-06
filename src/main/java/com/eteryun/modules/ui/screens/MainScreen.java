package com.eteryun.modules.ui.screens;

import com.eteryun.modules.cef.query.QueryTarget;
import com.eteryun.modules.cef.screen.CefScreen;
import com.google.gson.JsonObject;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.OptionsScreen;

public class MainScreen extends CefScreen {
    public MainScreen() {
        super("http://ui.eteryun.com.br/screens/");
    }

    @QueryTarget(name = "connect/start")
    public void connect(JsonObject object) {

    }

    @QueryTarget(name = "open/link")
    public void openUrl(String url) {
        Util.getPlatform().openUri(url);
    }

    @QueryTarget(name = "quit")
    public void quit(JsonObject object) {
        minecraft.stop();
    }

    @QueryTarget(name = "screen/settings")
    public void screenSettings(JsonObject object) {
        minecraft.setScreen(new OptionsScreen(this, minecraft.options));
    }
}
