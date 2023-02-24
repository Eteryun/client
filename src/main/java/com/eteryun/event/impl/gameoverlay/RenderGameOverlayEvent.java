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

    public enum ElementType {
        ALL, BOSSINFO, CHAT, PLAYER_LIST, DEBUG, HEALTH, HOTBAR, SPECTATOR_HOTBAR, EXPERIENCE, HEALTHMOUNT, JUMPBAR, EFFECTS, ITEM_NAME
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
}
