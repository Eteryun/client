package com.eteryun.event.impl.gameoverlay;

import com.eteryun.event.Event;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;

public class RenderGameOverlayEvent extends Event {
	
	public PoseStack getMatrixStack() {
		return mStack;
	}

	public float getPartialTicks() {
		return partialTicks;
	}

	public Window getWindow() {
		return window;
	}

	public ElementType getType() {
		return type;
	}

	public static enum ElementType {
		ALL, BOSSINFO, CHAT, PLAYER_LIST, DEBUG, AIR, HEALTH, FOOD, HOTBAR, SPECTATOR_HOTBAR, EXPERIENCE, HEALTHMOUNT, JUMPBAR
	}

	private final PoseStack mStack;
	private final float partialTicks;
	private final Window window;
	private final ElementType type;

	public RenderGameOverlayEvent(PoseStack mStack, float partialTicks, Window window) {
		this.mStack = mStack;
		this.partialTicks = partialTicks;
		this.window = window;
		this.type = null;
	}

	private RenderGameOverlayEvent(PoseStack mStack, RenderGameOverlayEvent parent, ElementType type) {
		this.mStack = mStack;
		this.partialTicks = parent.getPartialTicks();
		this.window = parent.getWindow();
		this.type = type;
	}

	public static class Pre extends RenderGameOverlayEvent {
		public Pre(PoseStack mStack, RenderGameOverlayEvent parent, ElementType type) {
			super(mStack, parent, type);
		}

		@Override
		public boolean isCancelable() {
			return true;
		}
	}

	public static class Post extends RenderGameOverlayEvent {
		public Post(PoseStack mStack, RenderGameOverlayEvent parent, ElementType type) {
			super(mStack, parent, type);
		}
	}

	public static class Chat extends Pre {
		private int posX;
		private int posY;

		public Chat(PoseStack mStack, RenderGameOverlayEvent parent, int posX, int posY) {
			super(mStack, parent, ElementType.CHAT);
			this.setPosX(posX);
			this.setPosY(posY);
		}

		public int getPosX() {
			return posX;
		}

		public void setPosX(int posX) {
			this.posX = posX;
		}

		public int getPosY() {
			return posY;
		}

		public void setPosY(int posY) {
			this.posY = posY;
		}
	}
}
