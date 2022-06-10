package com.eteryun.mixin.client;

import com.eteryun.Eteryun;
import com.eteryun.event.EventManager;
import com.eteryun.event.impl.tick.RenderTickEvent;
import com.eteryun.event.impl.tick.TickEvent;
import com.eteryun.screens.EmptyScreen;
import com.eteryun.util.EteryunConstants;
import com.ramon.ultralight.UltralightEngine;
import com.ramon.ultralight.View;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Timer;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    private boolean pause;

    @Shadow
    private float pausePartialTick;

    @Shadow
    @Final
    private Timer timer;

    private boolean isStarted = false;

    @Inject(method = "checkIs64Bit", at = @At("RETURN"))
    private static void client(CallbackInfoReturnable<Boolean> ci) {
        Eteryun.getInstance().init();
    }

    @Inject(method = "resizeDisplay", at = @At("RETURN"))
    private void startClient(CallbackInfo ci) {
        if (isStarted) return;

        Eteryun.getInstance().start();
        isStarted = true;
    }

    @Inject(method = "close", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/VirtualScreen;close()V"))
    private void close(CallbackInfo ci) {
        Eteryun.getInstance().shutdown();
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;render(FJZ)V", shift = At.Shift.BEFORE))
    private void runTickBefore(CallbackInfo ci) {
        RenderTickEvent event = new RenderTickEvent(TickEvent.Phase.START, this.pause ? this.pausePartialTick : this.timer.partialTick);
        EventManager.call(event);
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/toasts/ToastComponent;render(Lcom/mojang/blaze3d/vertex/PoseStack;)V", shift = At.Shift.AFTER))
    private void runTickAfter(CallbackInfo ci) {
        RenderTickEvent event = new RenderTickEvent(TickEvent.Phase.END, this.pause ? this.pausePartialTick : this.timer.partialTick);
        EventManager.call(event);
    }

    @Inject(method = "setScreen", at = @At(value = "HEAD"), cancellable = true)
    private void updateScreen(Screen screen, CallbackInfo ci) {
        View view = UltralightEngine.getActiveView();
        if (view instanceof View.ScreenView){
            UltralightEngine.getInstance().removeView(view.getName());
        }
    }

    /**
     * @author Eteryun
     */
    @Overwrite
    private String createTitle() {
        return EteryunConstants.ClientName + " v" + EteryunConstants.ClientVersion;
    }
}
