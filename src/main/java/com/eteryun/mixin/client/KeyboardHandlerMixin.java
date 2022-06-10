package com.eteryun.mixin.client;

import com.eteryun.event.EventManager;
import com.eteryun.event.impl.input.CharInputEvent;
import com.eteryun.event.impl.input.KeyInputEvent;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Inject(method = "keyPress", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;", shift = At.Shift.BEFORE, ordinal = 0))
    private void keyPress(long pWindowPointer, int pKey, int pScanCode, int pAction, int pMods, CallbackInfo callback) {
        KeyInputEvent event = new KeyInputEvent(pWindowPointer, pKey, pScanCode, pAction, pMods);
        EventManager.call(event);
    }

    @Inject(method = "charTyped", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;", shift = At.Shift.BEFORE, ordinal = 0))
    private void charTyped(long pWindowPointer, int pScanCode, int pMods, CallbackInfo callback) {
        CharInputEvent event = new CharInputEvent(pWindowPointer, pScanCode, pMods);
        EventManager.call(event);
    }
}
