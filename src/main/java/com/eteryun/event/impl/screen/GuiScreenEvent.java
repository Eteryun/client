package com.eteryun.event.impl.screen;

import com.eteryun.event.Event;

import net.minecraft.client.gui.screens.Screen;

public class GuiScreenEvent extends Event {
	
    private final Screen gui;

    public GuiScreenEvent(Screen gui)
    {
        this.gui = gui;
    }

    public Screen getGui()
    {
        return gui;
    }
}
