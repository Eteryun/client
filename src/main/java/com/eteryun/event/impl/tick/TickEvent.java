package com.eteryun.event.impl.tick;

import com.eteryun.event.Event;

public class TickEvent extends Event {
	public enum Type {
		WORLD, PLAYER, CLIENT, SERVER, RENDER;
	}

	public enum Phase {
		START, END;
	}

	public final Type type;
	public final Phase phase;

	public TickEvent(Type type, Phase phase) {
		this.type = type;
		this.phase = phase;
	}
}
