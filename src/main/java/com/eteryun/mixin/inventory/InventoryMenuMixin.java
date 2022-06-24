package com.eteryun.mixin.inventory;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin extends RecipeBookMenu<CraftingContainer> {
    public InventoryMenuMixin(MenuType<?> menuType, int i) {
        super(menuType, i);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void constructor(Inventory inventory, boolean bl, Player player, CallbackInfo ci) {
        this.addSlot(new Slot(inventory, 41, 77, 44) {
            public boolean mayPlace(ItemStack itemstack) {
                if (itemstack.getItem() instanceof SwordItem || itemstack.getItem() instanceof DiggerItem ||
                        itemstack.getItem() instanceof ProjectileWeaponItem)
                    return true;
                return false;
            }

            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS,  new ResourceLocation("eteryun", "item/empty_armor_slot_backtool"));
            }
        });
    }
}
