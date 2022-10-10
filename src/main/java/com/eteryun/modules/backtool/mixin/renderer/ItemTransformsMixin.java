package com.eteryun.modules.backtool.mixin.renderer;

import com.eteryun.modules.backtool.extension.ItemTransformsExtends;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

public class ItemTransformsMixin {
    @Mixin(targets = "net.minecraft.client.renderer.block.model.ItemTransforms$Deserializer")
    public abstract static class DeserializerMixin implements JsonDeserializer<ItemTransforms> {
        @Shadow
        public abstract ItemTransform getTransform(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject, String string);

        @Override
        public ItemTransforms deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            ItemTransform itemTransform = this.getTransform(jsonDeserializationContext, jsonObject, "thirdperson_righthand");
            ItemTransform itemTransform2 = this.getTransform(jsonDeserializationContext, jsonObject, "thirdperson_lefthand");
            if (itemTransform2 == ItemTransform.NO_TRANSFORM) {
                itemTransform2 = itemTransform;
            }

            ItemTransform itemTransform3 = this.getTransform(jsonDeserializationContext, jsonObject, "firstperson_righthand");
            ItemTransform itemTransform4 = this.getTransform(jsonDeserializationContext, jsonObject, "firstperson_lefthand");
            if (itemTransform4 == ItemTransform.NO_TRANSFORM) {
                itemTransform4 = itemTransform3;
            }

            ItemTransform itemTransform5 = this.getTransform(jsonDeserializationContext, jsonObject, "head");
            ItemTransform itemTransform6 = this.getTransform(jsonDeserializationContext, jsonObject, "gui");
            ItemTransform itemTransform7 = this.getTransform(jsonDeserializationContext, jsonObject, "ground");
            ItemTransform itemTransform8 = this.getTransform(jsonDeserializationContext, jsonObject, "fixed");
            ItemTransform itemTransform9 = this.getTransform(jsonDeserializationContext, jsonObject, "back");
            return new ItemTransformsExtends(itemTransform2, itemTransform, itemTransform4, itemTransform3, itemTransform5, itemTransform6, itemTransform7, itemTransform8, itemTransform9);
        }
    }

    @Mixin(ItemTransforms.TransformType.class)
    public static class TransformTypeMixin {
        @Shadow
        @Final
        @Mutable
        private static ItemTransforms.TransformType[] $VALUES;

        private static final ItemTransforms.TransformType BACK = transformType$addVariant("BACK");
        @Invoker("<init>")
        private static ItemTransforms.TransformType transformType$invokeInit(String internalName, int internalId) {
            throw new AssertionError();
        }

        private static ItemTransforms.TransformType transformType$addVariant(String internalName) {
            ArrayList<ItemTransforms.TransformType> variants = new ArrayList<ItemTransforms.TransformType>(Arrays.asList(TransformTypeMixin.$VALUES));
            ItemTransforms.TransformType transformType = transformType$invokeInit(internalName, variants.get(variants.size() - 1).ordinal() + 1);
            variants.add(transformType);
            TransformTypeMixin.$VALUES = variants.toArray(new ItemTransforms.TransformType[0]);
            return transformType;
        }
    }
}
