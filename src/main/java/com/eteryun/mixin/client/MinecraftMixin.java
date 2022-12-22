package com.eteryun.mixin.client;

import com.eteryun.Eteryun;
import com.eteryun.event.EventManager;
import com.eteryun.event.impl.screen.ScreenOpenEvent;
import com.eteryun.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow public abstract void setScreen(@Nullable Screen screen);

    private boolean isStarted = false;

    @Inject(method = "checkIs64Bit", at = @At("RETURN"))
    private static void client(CallbackInfoReturnable<Boolean> ci) {
        Eteryun.getInstance().loadModules();
        Eteryun.getInstance().preInit();
    }

    @Inject(method = "resizeDisplay", at = @At("RETURN"))
    private void startClient(CallbackInfo ci) {
        if (isStarted) return;

        Eteryun.getInstance().init();
        isStarted = true;
    }

    @Inject(method = "close", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/VirtualScreen;close()V", shift = At.Shift.BEFORE))
    private void close(CallbackInfo ci) {
        Eteryun.getInstance().shutdown();
    }

    @Inject(method = "createTitle", at = @At("HEAD"), cancellable = true)
    private void createTitle(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(Constants.CLIENT_NAME);
    }

    @Inject(method = "setScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;", shift = At.Shift.AFTER), cancellable = true)
    private void updateScreen(Screen screen, CallbackInfo ci) {
        ScreenOpenEvent event = new ScreenOpenEvent(screen);
        if (EventManager.call(event)) {
            ci.cancel();
        } else if (event.getScreen() != null && !event.getScreen().equals(screen)) {
            ci.cancel();
            setScreen(event.getScreen());
        }
    }
}
