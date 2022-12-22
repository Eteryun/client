package com.eteryun.modules.ui.screens;

import com.eteryun.modules.cef.query.QueryTarget;
import com.eteryun.modules.cef.screen.CefScreen;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class DisconnectScreen extends CefScreen {
    private final Screen lastScreen;

    public DisconnectScreen(Screen lastScreen, Component component, Component component2) {
        super(getUrl(component, component2), component);
        this.lastScreen = lastScreen;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    private static String getUrl(Component component, Component component2) {
        String url = "http://ui.eteryun.com.br/screens/#/disconnected?title=%s&reason=%s";
        return url.formatted(getTextFromComponent(component), getTextFromComponent(component2));
    }

    private static String getTextFromComponent(Component component) {
        return (component instanceof TranslatableComponent && ((TranslatableComponent) component).getArgs().length == 0) ? ((TranslatableComponent) component).getKey() : component.getString();
    }

    @QueryTarget(name = "return")
    public void ret(JsonObject object) {
        minecraft.setScreen(lastScreen);
    }
}
