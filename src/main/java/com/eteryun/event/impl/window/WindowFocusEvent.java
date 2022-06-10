package com.eteryun.event.impl.window;

public class WindowFocusEvent extends WindowEvent {

	private final boolean focused;

	public WindowFocusEvent(long window, boolean focused) {
		super(window);
		this.focused = focused;
	}

	public boolean isFocused() {
		return focused;
	}
}
