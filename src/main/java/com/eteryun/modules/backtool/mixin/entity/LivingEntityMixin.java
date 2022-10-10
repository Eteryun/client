package com.eteryun.modules.backtool.mixin.entity;

import com.eteryun.modules.backtool.extension.ILivingEntity;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ILivingEntity {
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract ItemStack getLastHandItem(EquipmentSlot equipmentSlot);

    @Shadow
    public abstract ItemStack getLastArmorItem(EquipmentSlot equipmentSlot);

    @Shadow
    public abstract ItemStack getItemBySlot(EquipmentSlot equipmentSlot);

    @Shadow
    public abstract AttributeMap getAttributes();

    @Shadow
    public abstract void setLastHandItem(EquipmentSlot equipmentSlot, ItemStack itemStack);

    @Shadow
    public abstract void setLastArmorItem(EquipmentSlot equipmentSlot, ItemStack itemStack);

    private final NonNullList<ItemStack> lastBackToolItemStacks = NonNullList.withSize(1, ItemStack.EMPTY);

    @Inject(method = "collectEquipmentChanges", at = @At("HEAD"), cancellable = true)
    public void collectEquipmentChanges(CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir) {
        Map<EquipmentSlot, ItemStack> map = null;

        for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
            ItemStack itemstack;
            switch (equipmentslot.getType()) {
                case HAND:
                    itemstack = this.getLastHandItem(equipmentslot);
                    break;
                case ARMOR:
                    itemstack = this.getLastArmorItem(equipmentslot);
                    break;
                default:
                    itemstack = this.getLastBackTool(equipmentslot);
            }

            ItemStack itemstack1 = this.getItemBySlot(equipmentslot);
            if (!ItemStack.matches(itemstack1, itemstack)) {
                if (map == null) {
                    map = Maps.newEnumMap(EquipmentSlot.class);
                }

                map.put(equipmentslot, itemstack1);
                if (!itemstack.isEmpty()) {
                    this.getAttributes().removeAttributeModifiers(itemstack.getAttributeModifiers(equipmentslot));
                }

                if (!itemstack1.isEmpty()) {
                    this.getAttributes().addTransientAttributeModifiers(itemstack1.getAttributeModifiers(equipmentslot));
                }
            }
        }

        cir.setReturnValue(map);
    }

    @Inject(method = "handleEquipmentChanges", at = @At("HEAD"), cancellable = true)
    public void handleEquipmentChanges(Map<EquipmentSlot, ItemStack> map, CallbackInfo ci) {
        ci.cancel();
        List<Pair<EquipmentSlot, ItemStack>> list = Lists.newArrayListWithCapacity(map.size());
        map.forEach((equipmentSlot, itemStack) -> {
            ItemStack itemStack2 = itemStack.copy();
            list.add(Pair.of(equipmentSlot, itemStack2));
            switch (equipmentSlot.getType()) {
                case HAND:
                    this.setLastHandItem(equipmentSlot, itemStack2);
                    break;
                case ARMOR:
                    this.setLastArmorItem(equipmentSlot, itemStack2);
            }

            if (equipmentSlot.getType().equals(EquipmentSlot.Type.valueOf("BACKTOOL")))
                this.setLastBackTool(equipmentSlot, itemStack2);
        });
        ((ServerLevel) level).getChunkSource().broadcast(this, new ClientboundSetEquipmentPacket(this.getId(), list));
    }

    @Override
    public ItemStack getLastBackTool(EquipmentSlot equipmentSlot) {
        return this.lastBackToolItemStacks.get(equipmentSlot.getIndex());
    }

    @Override
    public void setLastBackTool(EquipmentSlot equipmentSlot, ItemStack itemStack) {
        this.lastBackToolItemStacks.set(equipmentSlot.getIndex(), itemStack);
    }
}
