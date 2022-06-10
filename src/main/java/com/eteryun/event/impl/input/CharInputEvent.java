package com.eteryun.event.impl.input;

public class CharInputEvent extends InputEvent {
	
	private final int codePoint;
	private final int modifiers;

	public CharInputEvent(long window, int codePoint, int modifiers) {
		super(window);
		this.codePoint = codePoint;
		this.modifiers = modifiers;
	}

	public int getCodePoint() {
		return this.codePoint;
	}

	public int getModifiers() {
		return this.modifiers;
	}
}
