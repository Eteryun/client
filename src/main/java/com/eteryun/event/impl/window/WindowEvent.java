package com.eteryun.event.impl.window;

import com.eteryun.event.Event;

public class WindowEvent extends Event {
	private final long window;

	public WindowEvent(long window) {
		this.window = window;
	}

	public long getWindow() {
		return window;
	}
}
