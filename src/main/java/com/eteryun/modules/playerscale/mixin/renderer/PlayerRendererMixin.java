package com.eteryun.modules.playerscale.mixin.renderer;

import com.eteryun.modules.playerscale.extension.IPlayer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel<AbstractClientPlayer> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Shadow
    protected abstract void setModelProperties(AbstractClientPlayer pClientPlayer);

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(AbstractClientPlayer pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, CallbackInfo callback) {
        callback.cancel();
        this.setModelProperties(pEntity);
        IPlayer player = (IPlayer) pEntity;
        float scale = player.getScaleRender();
        pMatrixStack.pushPose();
        pMatrixStack.scale(scale, scale, scale);
        if (pEntity.isCrouching() && scale <= 0.2F) {
            pMatrixStack.translate(0, 1.0, 0);
        }
        shadowRadius = 0.5F * scale;
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
        pMatrixStack.popPose();
    }
}
