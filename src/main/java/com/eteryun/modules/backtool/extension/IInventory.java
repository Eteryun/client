package com.eteryun.modules.backtool.extension;

import net.minecraft.world.item.ItemStack;

public interface IInventory {
    ItemStack getBackToolSlot(int index);
    void setBackToolSlot(ItemStack stack, int index);
}
