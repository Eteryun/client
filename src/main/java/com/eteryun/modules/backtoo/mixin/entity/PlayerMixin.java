package com.eteryun.modules.backtoo.mixin.entity;

import com.eteryun.modules.backtoo.extension.IInventory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    @Shadow
    @Final
    private Inventory inventory;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "getItemBySlot", at = @At("HEAD"), cancellable = true)
    private void getItemBySlot(EquipmentSlot equipmentSlot, CallbackInfoReturnable<ItemStack> cir) {
        if (equipmentSlot.equals(EquipmentSlot.valueOf("BACKTOOL")))
            cir.setReturnValue(((IInventory) this.inventory).getBackToolSlot(0));
    }

    @Inject(method = "setItemSlot", at = @At("HEAD"), cancellable = true)
    private void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack, CallbackInfo ci) {
        if (equipmentSlot.equals(EquipmentSlot.valueOf("BACKTOOL"))) {
            ci.cancel();
            this.verifyEquippedItem(itemStack);
            this.equipEventAndSound(itemStack);
            ((IInventory) this.inventory).setBackToolSlot(itemStack, 0);
        }
    }
}
