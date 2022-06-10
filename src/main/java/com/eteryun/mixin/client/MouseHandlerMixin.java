package com.eteryun.mixin.client;

import com.eteryun.event.EventManager;
import com.eteryun.event.impl.input.MouseCursorEvent;
import com.eteryun.event.impl.input.MouseInputEvent;
import com.eteryun.event.impl.input.MouseScrollEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Shadow
    private boolean isLeftPressed;
    @Shadow
    private boolean isMiddlePressed;
    @Shadow
    private boolean isRightPressed;

    private Minecraft minecraft = Minecraft.getInstance();

    @Inject(method = "onPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getOverlay()Lnet/minecraft/client/gui/screens/Overlay;", shift = At.Shift.BEFORE))
    public void onPress(long pWindowPointer, int p_91532_, int pButton, int pAction, CallbackInfo ci) {
        MouseInputEvent event = new MouseInputEvent(pWindowPointer, pAction, pButton, pAction);
        EventManager.call(event);
    }

    @Inject(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getOverlay()Lnet/minecraft/client/gui/screens/Overlay;", shift = At.Shift.BEFORE))
    public void onScroll(long pWindowPointer, double pXOffset, double pYOffset, CallbackInfo ci) {
        double scrollDelta = (this.minecraft.options.discreteMouseScroll ? Math.signum(pYOffset) : pYOffset) * this.minecraft.options.mouseWheelSensitivity;
        MouseScrollEvent event = new MouseScrollEvent(pWindowPointer, scrollDelta, this.isLeftPressed, this.isMiddlePressed, this.isRightPressed, pXOffset,
                pYOffset);
        EventManager.call(event);
    }

    @Inject(method = "onMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getOverlay()Lnet/minecraft/client/gui/screens/Overlay;", shift = At.Shift.BEFORE))
    public void onMove(long pWindowPointer, double pXpos, double pYpos, CallbackInfo ci) {
        MouseCursorEvent event = new MouseCursorEvent(pWindowPointer, pXpos, pYpos);
        EventManager.call(event);
    }
}
