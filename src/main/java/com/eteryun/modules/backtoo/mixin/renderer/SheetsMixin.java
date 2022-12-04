package com.eteryun.modules.backtoo.mixin.renderer;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(Sheets.class)
public class SheetsMixin {
    @Inject(method = "getAllMaterials", at = @At("RETURN"))
    private static void getAllMaterials(Consumer<Material> consumer, CallbackInfo ci){
        consumer.accept(new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("eteryun", "item/empty_armor_slot_backtool")));
    }
}
