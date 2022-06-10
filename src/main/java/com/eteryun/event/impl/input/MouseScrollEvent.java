package com.eteryun.event.impl.input;

public class MouseScrollEvent extends InputEvent {

	private final double scrollDelta;
	private final double xDelta;
	private final double yDelta;
	private final boolean leftDown;
	private final boolean middleDown;
	private final boolean rightDown;

	public MouseScrollEvent(long window, double scrollDelta, boolean leftDown, boolean middleDown, boolean rightDown,
			double xDelta, double yDelta) {
		super(window);
		this.scrollDelta = scrollDelta;
		this.leftDown = leftDown;
		this.middleDown = middleDown;
		this.rightDown = rightDown;
		this.xDelta = xDelta;
		this.yDelta = yDelta;
	}

	public double getScrollDelta() {
		return this.scrollDelta;
	}

	public boolean isLeftDown() {
		return this.leftDown;
	}

	public boolean isRightDown() {
		return this.rightDown;
	}

	public boolean isMiddleDown() {
		return this.middleDown;
	}

	public double getXDelta() {
		return this.xDelta;
	}

	public double getYDelta() {
		return this.yDelta;
	}
}
