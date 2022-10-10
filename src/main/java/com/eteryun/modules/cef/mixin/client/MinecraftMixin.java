package com.eteryun.modules.cef.mixin.client;

import com.eteryun.modules.cef.CefManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void init(GameConfig gameConfig, CallbackInfo ci) {
        CefManager.init();
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/renderer/GameRenderer;render(FJZ)V", shift = At.Shift.BEFORE))
    public void runTick(boolean bl, CallbackInfo ci) {
        CefManager.update();
    }
}
