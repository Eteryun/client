package com.eteryun.modules.cef.mixin.cef;

import com.eteryun.modules.cef.extension.CefAppAccess;
import org.cef.CefApp;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CefApp.class)
public abstract class CefAppMixin implements CefAppAccess {
    @Shadow protected abstract void N_DoMessageLoopWork();

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljavax/swing/SwingUtilities;isEventDispatchThread()Z"), remap = false)
    private boolean initIsEventDispatchThread() {
        return true;
    }

    @Redirect(method = "initialize", at = @At(value = "INVOKE", target = "Ljavax/swing/SwingUtilities;isEventDispatchThread()Z"), remap = false)
    private boolean initializeIsEventDispatchThread() {
        return true;
    }

    @Redirect(method = "setState", at = @At(value = "INVOKE", target = "Ljavax/swing/SwingUtilities;invokeLater(Ljava/lang/Runnable;)V"), remap = false)
    private static void setStateInvokeLater(Runnable runnable) {
       runnable.run();
    }

    @Redirect(method = "handleBeforeTerminate", at = @At(value = "INVOKE", target = "Ljavax/swing/SwingUtilities;invokeLater(Ljava/lang/Runnable;)V"), remap = false)
    private void handleBeforeTerminateInvokeLater(Runnable runnable) {
        runnable.run();
    }

    @Redirect(method = "shutdown", at = @At(value = "INVOKE", target = "Ljavax/swing/SwingUtilities;invokeLater(Ljava/lang/Runnable;)V"), remap = false)
    private void shutdownInvokeLater(Runnable runnable) {
        runnable.run();
    }

    @Override
    public void doLoopWork() {
        N_DoMessageLoopWork();
    }
}
