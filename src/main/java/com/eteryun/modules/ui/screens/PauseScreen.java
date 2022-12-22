package com.eteryun.modules.ui.screens;

import com.eteryun.modules.cef.query.QueryTarget;
import com.eteryun.modules.cef.screen.CefScreen;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.TitleScreen;

public class PauseScreen extends CefScreen {

    public PauseScreen() {
        super("http://ui.eteryun.com.br/screens/#/pause");
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @QueryTarget(name = "close")
    public void close(JsonObject object) {
        this.minecraft.setScreen(null);
        this.minecraft.mouseHandler.grabMouse();
    }

    @QueryTarget(name = "screen/settings")
    public void screenSettings(JsonObject object) {
        minecraft.setScreen(new OptionsScreen(this, minecraft.options));
    }

    @QueryTarget(name = "connect/disconnect")
    public void quit(JsonObject object) {
        this.minecraft.level.disconnect();
        this.minecraft.clearLevel();
        this.minecraft.setScreen(new TitleScreen());
    }
}
