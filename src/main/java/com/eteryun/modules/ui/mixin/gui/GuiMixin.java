package com.eteryun.modules.ui.mixin.gui;

import com.eteryun.event.EventManager;
import com.eteryun.event.impl.gameoverlay.RenderGameOverlayEvent;
import com.eteryun.event.impl.gameoverlay.RenderGameOverlayEvent.ElementType;
import com.eteryun.event.impl.gameoverlay.TickGameOverlayEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    private RenderGameOverlayEvent eventParent;
    @Shadow
    @Final
    private Minecraft minecraft;
    private boolean bl;

    @Shadow
    protected abstract void renderHotbar(float f, PoseStack poseStack);

    @Shadow
    public abstract void renderJumpMeter(PoseStack poseStack, int i);

    @Shadow
    public abstract void renderExperienceBar(PoseStack poseStack, int i);

    @Shadow
    protected abstract void renderHearts(PoseStack poseStack, Player player, int i, int j, int k, int l, float f, int m, int n, int o, boolean bl);

    @Shadow protected abstract void renderPlayerHealth(PoseStack poseStack);

    @Shadow protected abstract void renderVehicleHealth(PoseStack poseStack);

    @Shadow protected abstract void renderEffects(PoseStack poseStack);

    @Shadow private @Nullable Component overlayMessageString;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void preAllRender(PoseStack poseStack, float partialTicks, CallbackInfo ci) {
        eventParent = new RenderGameOverlayEvent(poseStack, partialTicks, minecraft.getWindow());
        if (pre(ElementType.ALL, poseStack))
            ci.cancel();
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void postAllRender(PoseStack poseStack, float partialTicks, CallbackInfo ci) {
        post(ElementType.ALL, poseStack);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/spectator/SpectatorGui;renderHotbar(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private void SpectatorHotbar(SpectatorGui instance, PoseStack poseStack) {
        if (!pre(ElementType.SPECTATOR_HOTBAR, poseStack)) {
            instance.renderHotbar(poseStack);
            post(ElementType.SPECTATOR_HOTBAR, poseStack);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHotbar(FLcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private void Hotbar(Gui instance, float f, PoseStack poseStack) {
        if (!pre(ElementType.HOTBAR, poseStack)) {
            this.renderHotbar(f, poseStack);
            post(ElementType.HOTBAR, poseStack);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderJumpMeter(Lcom/mojang/blaze3d/vertex/PoseStack;I)V"))
    private void JumpBar(Gui instance, PoseStack poseStack, int i) {
        if (!pre(ElementType.JUMPBAR, poseStack)) {
            this.renderJumpMeter(poseStack, i);
            post(ElementType.JUMPBAR, poseStack);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderExperienceBar(Lcom/mojang/blaze3d/vertex/PoseStack;I)V"))
    private void Experience(Gui instance, PoseStack poseStack, int i) {
        if (!pre(ElementType.EXPERIENCE, poseStack)) {
            this.renderExperienceBar(poseStack, i);
            post(ElementType.EXPERIENCE, poseStack);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;render(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private void Debug(DebugScreenOverlay instance, PoseStack poseStack) {
        if (!pre(ElementType.DEBUG, poseStack)) {
            instance.render(poseStack);
            post(ElementType.DEBUG, poseStack);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderEffects(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private void Effects(Gui instance, PoseStack poseStack) {
        if (!pre(ElementType.EFFECTS, poseStack)) {
            this.renderEffects(poseStack);
            post(ElementType.EFFECTS, poseStack);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lcom/mojang/blaze3d/vertex/PoseStack;I)V"))
    private void Chat(ChatComponent instance, PoseStack poseStack, int i) {
        if (!pre(ElementType.CHAT, poseStack)) {
            instance.render(poseStack, i);
            post(ElementType.CHAT, poseStack);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/PlayerTabOverlay;render(Lcom/mojang/blaze3d/vertex/PoseStack;ILnet/minecraft/world/scores/Scoreboard;Lnet/minecraft/world/scores/Objective;)V"))
    private void TabList(PlayerTabOverlay instance, PoseStack poseStack, int i, Scoreboard scoreboard, Objective objective) {
        if (!pre(ElementType.PLAYER_LIST, poseStack)) {
            instance.render(poseStack, i, scoreboard, objective);
            post(ElementType.PLAYER_LIST, poseStack);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderPlayerHealth(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private void RenderHealth(Gui instance, PoseStack poseStack) {
        if (!pre(ElementType.HEALTH, poseStack)) {
            this.renderPlayerHealth(poseStack);
            post(ElementType.HEALTH, poseStack);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderVehicleHealth(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private void RenderHealthMount(Gui instance, PoseStack poseStack) {
        if (!pre(ElementType.HEALTHMOUNT, poseStack)) {
            this.renderVehicleHealth(poseStack);
            post(ElementType.HEALTHMOUNT, poseStack);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderSelectedItemName(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private void RenderItemName(Gui instance, PoseStack poseStack) {
        if (!pre(ElementType.ITEM_NAME, poseStack)) {
            instance.renderSelectedItemName(poseStack);
            post(ElementType.ITEM_NAME, poseStack);
        }
    }

    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Gui;overlayMessageString:Lnet/minecraft/network/chat/Component;",  opcode = Opcodes.GETFIELD))
    private Component getOverlayMessage(Gui instance) {
        return null;
    }

    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Gui;title:Lnet/minecraft/network/chat/Component;",  opcode = Opcodes.GETFIELD))
    private Component getTitleMessage(Gui instance) {
        return null;
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void Tick(CallbackInfo ci) {
        TickGameOverlayEvent event = new TickGameOverlayEvent();
        EventManager.call(event);
    }

    private boolean pre(ElementType type, PoseStack mStack) {
        return EventManager.call(new RenderGameOverlayEvent.Pre(mStack, eventParent, type));
    }

    private void post(ElementType type, PoseStack mStack) {
        EventManager.call(new RenderGameOverlayEvent.Post(mStack, eventParent, type));
    }
}
