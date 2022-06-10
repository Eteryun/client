package com.eteryun.event.impl.input;

import com.eteryun.event.Event;

public class InputEvent extends Event {
	private final long window;

	public InputEvent(long window) {
		this.window = window;
	}

	public long getWindow() {
		return window;
	}
}
