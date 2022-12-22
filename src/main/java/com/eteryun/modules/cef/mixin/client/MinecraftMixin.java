package com.eteryun.modules.cef.mixin.client;

import com.eteryun.modules.cef.CefManager;
import com.eteryun.modules.cef.screen.CefScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow private ProfilerFiller profiler;

    @Inject(method = "runTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;noRender:Z", shift = At.Shift.AFTER))
    public void runTick(boolean bl, CallbackInfo ci) {
        profiler.popPush("Cef");
        CefManager.update();
    }
}
