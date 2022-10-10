package com.eteryun.modules.backtool.extension;

import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;

public class ItemTransformsExtends extends ItemTransforms {
    public ItemTransform back = ItemTransform.NO_TRANSFORM;

    public ItemTransformsExtends(ItemTransforms itemTransforms) {
        super(itemTransforms);
    }

    public ItemTransformsExtends(ItemTransform itemTransform, ItemTransform itemTransform2, ItemTransform itemTransform3, ItemTransform itemTransform4, ItemTransform itemTransform5, ItemTransform itemTransform6, ItemTransform itemTransform7, ItemTransform itemTransform8) {
        super(itemTransform, itemTransform2, itemTransform3, itemTransform4, itemTransform5, itemTransform6, itemTransform7, itemTransform8);
    }

    public ItemTransformsExtends(ItemTransform itemTransform, ItemTransform itemTransform2, ItemTransform itemTransform3, ItemTransform itemTransform4, ItemTransform itemTransform5, ItemTransform itemTransform6, ItemTransform itemTransform7, ItemTransform itemTransform8, ItemTransform itemTransform9) {
        super(itemTransform, itemTransform2, itemTransform3, itemTransform4, itemTransform5, itemTransform6, itemTransform7, itemTransform8);
        this.back = itemTransform9;
    }

    @Override
    public ItemTransform getTransform(ItemTransforms.TransformType transformType) {
        if (transformType == ItemTransforms.TransformType.valueOf("BACK")) {
            return this.back;
        }
        return super.getTransform(transformType);
    }
}
