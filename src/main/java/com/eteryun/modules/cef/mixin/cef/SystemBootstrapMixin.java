package com.eteryun.modules.cef.mixin.cef;

import org.cef.SystemBootstrap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SystemBootstrap.class)
public class SystemBootstrapMixin {
    @Inject(method = "loadLibrary", at = @At("HEAD"), remap = false, cancellable = true)
    private static void loadLibrary(CallbackInfo ci) {
        ci.cancel();
    }
}
