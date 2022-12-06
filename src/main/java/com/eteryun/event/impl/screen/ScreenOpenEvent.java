package com.eteryun.event.impl.screen;

import com.eteryun.event.Event;
import net.minecraft.client.gui.screens.Screen;

public class ScreenOpenEvent extends Event {
    private Screen screen;

    public ScreenOpenEvent(Screen screen) {
        this.screen = screen;
    }

    public Screen getScreen()
    {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}