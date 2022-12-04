package com.eteryun.modules.backtoo.mixin.entity;

import com.eteryun.modules.backtoo.extension.IInventory;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(Inventory.class)
public class InventoryMixin implements IInventory {
    private NonNullList<ItemStack> backtool;

    @Shadow
    @Final
    @Mutable
    private List<NonNullList<ItemStack>> compartments;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void constructor(Player player, CallbackInfo ci) {
        this.backtool = NonNullList.withSize(1, ItemStack.EMPTY);
        this.compartments = new ArrayList<>(this.compartments);
        this.compartments.add(backtool);
        this.compartments = ImmutableList.copyOf(this.compartments);
    }

    @Inject(method = "save", at = @At("TAIL"))
    public void save(ListTag listTag, CallbackInfoReturnable<ListTag> cir) {
        this.backtool.forEach((backtool) -> {
            if (!backtool.isEmpty()) {
                CompoundTag compoundtag2 = new CompoundTag();
                compoundtag2.putByte("Slot", (byte) (this.backtool.indexOf(backtool) + 200));
                backtool.save(compoundtag2);
                listTag.add(compoundtag2);
            }
        });
    }

    @Inject(method = "load", at = @At("TAIL"))
    public void load(ListTag listTag, CallbackInfo ci) {
        this.backtool.clear();

        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            int slot = compoundTag.getByte("Slot") & 255;
            ItemStack itemStack = ItemStack.of(compoundTag);
            if (!itemStack.isEmpty()) {
                if (slot >= 200 && slot < this.backtool.size() + 200) {
                    this.backtool.set(slot - 200, itemStack);
                }
            }
        }
    }

    @Inject(method = "getContainerSize", at = @At("HEAD"), cancellable = true)
    public void getContainerSize(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(compartments.stream().collect(Collectors.summingInt(NonNullList::size)));
    }

    @Inject(method = "isEmpty", at = @At("HEAD"), cancellable = true)
    public void isEmpty(CallbackInfoReturnable<Boolean> cir) {
        if (!this.backtool.isEmpty()) {
            for (ItemStack itemstack : this.backtool) {
                if (!itemstack.isEmpty()) {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Override
    public ItemStack getBackToolSlot(int index) {
        return this.backtool.get(index);
    }

    @Override
    public void setBackToolSlot(ItemStack stack, int index) {
        this.backtool.set(index, stack);
    }
}
