package com.eteryun.modules.cef.mixin.client;

import com.eteryun.modules.cef.CefManager;
import com.eteryun.modules.cef.screen.CefScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    public abstract void setScreen(@Nullable Screen screen);

    @Inject(method = "resizeDisplay", at = @At(value = "RETURN"))
    private void resizeDisplay(CallbackInfo ci) {
        CefManager.init();
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/renderer/GameRenderer;render(FJZ)V", shift = At.Shift.BEFORE))
    public void runTick(boolean bl, CallbackInfo ci) {
        CefManager.update();
    }

    @Inject(method = "setScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;", shift = At.Shift.AFTER), cancellable = true)
    private void updateScreen(Screen screen, CallbackInfo ci) {
        if (screen instanceof OptionsScreen) {
            ci.cancel();
            setScreen(new CefScreen());
        }
    }
}
