package com.eteryun.modules.backtoo.render.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;

public class BacktooLayer<T extends Player, M extends HumanoidModel<T>, A extends HumanoidModel<T>>
        extends RenderLayer<T, M> {
    public BacktooLayer(RenderLayerParent<T, M> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, T entity, float f, float g, float h, float j, float k, float l) {
        float offset = 0;
        if (!entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty())
            offset += 1.0F;

        if (entity.isModelPartShown(PlayerModelPart.JACKET))
            offset += 0.5F;

        poseStack.pushPose();
        ModelPart modelpart = this.getParentModel().body;
        ItemStack itemstack = entity.getItemBySlot(EquipmentSlot.valueOf("BACKTOOL"));

        poseStack.translate(0F, 4F / 16F, 1.91F / 16F + (offset / 16F));

        poseStack.mulPose(Vector3f.ZP.rotation(modelpart.zRot));
        poseStack.mulPose(Vector3f.YP.rotation(modelpart.yRot));
        poseStack.mulPose(Vector3f.XP.rotation(modelpart.xRot));

        Minecraft.getInstance().getItemInHandRenderer().renderItem(entity, itemstack, ItemTransforms.TransformType.valueOf("BACK"), false, poseStack, multiBufferSource, i);
        poseStack.popPose();
    }
}