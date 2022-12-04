package com.eteryun.modules.backtoo.extension;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public interface ILivingEntity {
    ItemStack getLastBackTool(EquipmentSlot equipmentSlot);
    void setLastBackTool(EquipmentSlot equipmentSlot, ItemStack itemStack);
}
