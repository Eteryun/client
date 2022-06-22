package com.eteryun.extensions;

import net.minecraft.world.item.ItemStack;

public interface IInventory {
    ItemStack getBackToolSlot(int index);
    void setBackToolSlot(ItemStack stack, int index);
}
