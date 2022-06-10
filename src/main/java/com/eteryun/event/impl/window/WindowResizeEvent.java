package com.eteryun.event.impl.window;

public class WindowResizeEvent extends WindowEvent {

	private final int width;
	private final int height;

	public WindowResizeEvent(long window, int width, int height) {
		super(window);
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
