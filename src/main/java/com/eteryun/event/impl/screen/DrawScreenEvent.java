package com.eteryun.event.impl.screen;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.Screen;

public class DrawScreenEvent extends GuiScreenEvent {
	
	private final PoseStack mStack;
	private final int mouseX;
	private final int mouseY;
	private final float renderPartialTicks;

	public DrawScreenEvent(Screen gui, PoseStack mStack, int mouseX, int mouseY, float renderPartialTicks) {
		super(gui);
		this.mStack = mStack;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.renderPartialTicks = renderPartialTicks;
	}

	public PoseStack getMatrixStack() {
		return mStack;
	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public float getRenderPartialTicks() {
		return renderPartialTicks;
	}

    public static class Pre extends DrawScreenEvent
    {
        public Pre(Screen gui, PoseStack mStack, int mouseX, int mouseY, float renderPartialTicks)
        {
            super(gui, mStack, mouseX, mouseY, renderPartialTicks);
        }

    	@Override
    	public boolean isCancelable() {
    		return true;
    	}
    }

    public static class Post extends DrawScreenEvent
    {
        public Post(Screen gui, PoseStack mStack, int mouseX, int mouseY, float renderPartialTicks)
        {
            super(gui, mStack, mouseX, mouseY, renderPartialTicks);
        }
    }
}
