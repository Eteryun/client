package com.eteryun.event.impl.input;

public class MouseCursorEvent extends InputEvent {

	private final double mouseX;
	private final double mouseY;

	public MouseCursorEvent(long window, double mouseX, double mouseY) {
		super(window);
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}

	public double getMouseX() {
		return this.mouseX;
	}

	public double getMouseY() {
		return this.mouseY;
	}
}
