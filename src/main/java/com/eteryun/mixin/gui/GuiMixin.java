package com.eteryun.mixin.gui;

import com.eteryun.event.EventManager;
import com.eteryun.event.impl.gameoverlay.RenderGameOverlayEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    private RenderGameOverlayEvent eventParent;
    private Minecraft minecraft = Minecraft.getInstance();

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void renderHead(PoseStack stack, float partialTicks, CallbackInfo ci) {
        eventParent = new RenderGameOverlayEvent(stack, partialTicks, this.minecraft.getWindow());
        if (pre(RenderGameOverlayEvent.ElementType.ALL, stack))
            ci.cancel();
    }

    @Inject(method = "render", at = @At("RETURN"))
    public void renderReturn(PoseStack stack, float partialTicks, CallbackInfo ci) {
        eventParent = new RenderGameOverlayEvent(stack, partialTicks, this.minecraft.getWindow());
        post(RenderGameOverlayEvent.ElementType.ALL, stack);
    }

    private boolean pre(RenderGameOverlayEvent.ElementType type, PoseStack mStack) {
        return EventManager.call(new RenderGameOverlayEvent.Pre(mStack, eventParent, type));
    }

    private void post(RenderGameOverlayEvent.ElementType type, PoseStack mStack) {
        EventManager.call(new RenderGameOverlayEvent.Post(mStack, eventParent, type));
    }
}
