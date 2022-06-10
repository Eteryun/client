package com.eteryun.event.impl.tick;

public class RenderTickEvent extends TickEvent {
	public final float renderTickTime;

	public RenderTickEvent(Phase phase, float renderTickTime) {
		super(Type.RENDER, phase);
		this.renderTickTime = renderTickTime;
	}
}
