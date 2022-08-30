package com.eteryun.event.impl.screen;

import com.eteryun.event.Event;
import net.minecraft.client.gui.screens.Screen;

public class GuiOpenEvent extends Event {
    private Screen gui;

    public GuiOpenEvent(Screen gui) {
            this.gui = gui;
    }

    public Screen getGui()
    {
        return gui;
    }

    public void setGui(Screen gui) {
        this.gui = gui;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
