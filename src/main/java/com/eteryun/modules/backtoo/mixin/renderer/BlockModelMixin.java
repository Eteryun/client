package com.eteryun.modules.backtoo.mixin.renderer;

import com.eteryun.modules.backtoo.extension.ItemTransformsExtends;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockModel.class)
public abstract class BlockModelMixin {
    @Shadow
    public abstract ItemTransform getTransform(ItemTransforms.TransformType transformType);

    @Redirect(method = "getTransforms", at = @At(
            value = "NEW", target = "net/minecraft/client/renderer/block/model/ItemTransforms"))
    public ItemTransforms getTransforms(ItemTransform itemTransform, ItemTransform itemTransform2, ItemTransform itemTransform3, ItemTransform itemTransform4, ItemTransform itemTransform5, ItemTransform itemTransform6, ItemTransform itemTransform7, ItemTransform itemTransform8) {
        ItemTransform itemTransform9 = this.getTransform(ItemTransforms.TransformType.valueOf("BACK"));
        return new ItemTransformsExtends(itemTransform, itemTransform2, itemTransform3, itemTransform4, itemTransform5, itemTransform6, itemTransform7, itemTransform8, itemTransform9);
    }
}
