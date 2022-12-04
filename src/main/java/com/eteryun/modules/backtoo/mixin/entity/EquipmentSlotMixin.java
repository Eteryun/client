package com.eteryun.modules.backtoo.mixin.entity;

import net.minecraft.world.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(EquipmentSlot.class)
public class EquipmentSlotMixin {
    @Shadow
    @Final
    @Mutable
    private static EquipmentSlot[] $VALUES;

    private static final EquipmentSlot BACKTOOL = equipmentSlot$addVariant("BACKTOOL", EquipmentSlot.Type.valueOf("BACKTOOL"), 0, 0, "backtool");

    @Invoker("<init>")
    private static EquipmentSlot equipmentSlot$invokeInit(String internalName, int internalId, EquipmentSlot.Type type, int index, int filterFlag, String name) {
        throw new AssertionError();
    }

    private static EquipmentSlot equipmentSlot$addVariant(String internalName, EquipmentSlot.Type type, int index, int filterFlag, String name) {
        ArrayList<EquipmentSlot> variants = new ArrayList<EquipmentSlot>(Arrays.asList(EquipmentSlotMixin.$VALUES));
        EquipmentSlot equipmentSlot = equipmentSlot$invokeInit(internalName, variants.get(variants.size() - 1).ordinal() + 1, type, index, filterFlag, name);
        variants.add(equipmentSlot);
        EquipmentSlotMixin.$VALUES = variants.toArray(new EquipmentSlot[0]);
        return equipmentSlot;
    }

    @Mixin(EquipmentSlot.Type.class)
    public static class TypeMixin {
        @Shadow
        @Final
        @Mutable
        private static EquipmentSlot.Type[] $VALUES;

        private static final EquipmentSlot.Type BACKTOOL = equipmentSlotType$addVariant("BACKTOOL");

        @Invoker("<init>")
        private static EquipmentSlot.Type equipmentSlotType$invokeInit(String internalName, int internalId) {
            throw new AssertionError();
        }

        private static EquipmentSlot.Type equipmentSlotType$addVariant(String internalName) {
            ArrayList<EquipmentSlot.Type> variants = new ArrayList<EquipmentSlot.Type>(Arrays.asList(TypeMixin.$VALUES));
            EquipmentSlot.Type equipmentSlotType = equipmentSlotType$invokeInit(internalName, variants.get(variants.size() - 1).ordinal() + 1);
            variants.add(equipmentSlotType);
            TypeMixin.$VALUES = variants.toArray(new EquipmentSlot.Type[0]);
            return equipmentSlotType;
        }
    }
}
