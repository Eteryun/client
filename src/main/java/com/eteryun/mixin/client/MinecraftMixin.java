package com.eteryun.mixin.client;

import com.eteryun.Eteryun;
import com.eteryun.util.EteryunConstants;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {
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

    /**
     * @author Eteryun
     */
    @Overwrite
    private String createTitle() {
        return EteryunConstants.ClientName + " v" + EteryunConstants.ClientVersion;
    }
}
