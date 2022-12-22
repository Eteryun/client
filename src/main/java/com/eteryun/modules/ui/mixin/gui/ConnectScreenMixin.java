package com.eteryun.modules.ui.mixin.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public class ConnectScreenMixin {
    @Inject(method = "startConnecting", at = @At("HEAD"), cancellable = true)
    private static void startConnecting(Screen screen, Minecraft minecraft, ServerAddress serverAddress, ServerData serverData, CallbackInfo ci) {
        ci.cancel();
        com.eteryun.modules.ui.screens.ConnectScreen.connecting(screen, minecraft, serverAddress, serverData);
    }
}
