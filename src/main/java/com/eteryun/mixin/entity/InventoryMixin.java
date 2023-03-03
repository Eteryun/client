package com.eteryun.mixin.entity;

import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public class InventoryMixin {
    @Inject(method = "getSelectionSize", at = @At("RETURN"), cancellable = true)
    private static void getSelectionSizeMixin(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(8);
    }

    @ModifyConstant(method = "isHotbarSlot", constant = @Constant(intValue = 9))
    private static int isHotbarSlotMixin(int constant) {
        return 8;
    }

    @ModifyConstant(method = "getSuitableHotbarSlot", constant = @Constant(intValue = 9))
    private static int getSuitableHotbarSlotMixin(int constant) {
        return 8;
    }

    @ModifyConstant(method = "swapPaint", constant = @Constant(intValue = 9))
    private static int swapPaintMixin(int constant) {
        return 8;
    }
}
