package com.eteryun.event.impl.input;

public class MouseInputEvent extends InputEvent {
	
	private final int button;
    private final int action;
    private final int mods;
    public MouseInputEvent(long window, int button, int action, int mods)
    {
    	super(window);
        this.button = button;
        this.action = action;
        this.mods = mods;
    }

    public int getButton()
    {
        return this.button;
    }

    public int getAction()
    {
        return this.action;
    }

    public int getMods()
    {
        return this.mods;
    }
}
