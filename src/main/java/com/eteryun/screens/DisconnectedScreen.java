package com.eteryun.screens;

import com.ramon.ultralight.UltralightEngine;
import com.ramon.ultralight.UltralightResources;
import com.ramon.ultralight.View;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class DisconnectedScreen extends Screen {
    private final Component reason;
    private final Screen parent;
    private View.ScreenView view;

    protected DisconnectedScreen(Screen screen, Component component, Component component2) {
        super(component);
        this.parent = screen;
        this.reason = component2;

        this.view = UltralightEngine.getInstance().newScreenView("disconnected", this, this, this);
    }

    @Override
    protected void init() {
        super.init();
        String parsedTitle = title.getString().replaceAll("\n", "/n");
        String parsedReason = reason.getString().replaceAll("\n", "/n");
        view.loadUrl(UltralightResources.getNUI("screens", "index.html", "disconnected?title=" + parsedTitle + "&reason=" + parsedReason));
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }
}
