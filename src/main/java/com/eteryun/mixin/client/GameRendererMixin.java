package com.eteryun.mixin.client;

import com.eteryun.event.EventManager;
import com.eteryun.event.impl.screen.DrawScreenEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Redirect(method = "render",  at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V"))
    public void screenRender(Screen screen, PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        DrawScreenEvent.Pre preEvent = new DrawScreenEvent.Pre(screen, pPoseStack, pMouseY, pMouseY, pPartialTick);
        if (!EventManager.call(preEvent))
            screen.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        DrawScreenEvent.Post postEvent = new DrawScreenEvent.Post(screen, pPoseStack, pMouseX, pMouseY, pPartialTick);
        EventManager.call(postEvent);
    }
}
