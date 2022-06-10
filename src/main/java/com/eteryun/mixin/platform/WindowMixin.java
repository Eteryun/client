package com.eteryun.mixin.platform;

import com.eteryun.event.EventManager;
import com.eteryun.event.impl.window.WindowFocusEvent;
import com.eteryun.event.impl.window.WindowResizeEvent;
import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class WindowMixin {
    @Inject(method = "onResize", at = @At("HEAD"))
    public void onResize(long window, int width, int height, CallbackInfo ci) {
        WindowResizeEvent event = new WindowResizeEvent(window, width, height);
        EventManager.call(event);
    }

    @Inject(method = "onFocus", at = @At("HEAD"))
    public void onFocus(long window, boolean focused, CallbackInfo ci) {
        WindowFocusEvent event = new WindowFocusEvent(window, focused);
        EventManager.call(event);
    }
}
