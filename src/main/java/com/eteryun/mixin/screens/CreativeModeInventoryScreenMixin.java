package com.eteryun.mixin.screens;

import net.minecraft.client.HotbarManager;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CreativeModeInventoryScreen.class)
public class CreativeModeInventoryScreenMixin {
    @ModifyConstant(method = "selectTab", constant = {@Constant(intValue = 9, ordinal = 0), @Constant(intValue = 9, ordinal = 1)})
    private int selectTabMixin(int constant) {
        return HotbarManager.NUM_HOTBAR_GROUPS;
    }
}
