package com.eteryun.mixin.screens;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
    @ModifyConstant(method = "checkHotbarMouseClicked", constant = @Constant(intValue = 9))
    private int checkHotbarMouseClickedMixin(int constant) {
        return 8;
    }

    @ModifyConstant(method = "checkHotbarKeyPressed", constant = @Constant(intValue = 9))
    private int checkHotbarKeyPressedMixin(int constant) {
        return 8;
    }
}
