package com.eteryun.mixin.client;

import net.minecraft.client.HotbarManager;
import net.minecraft.client.player.inventory.Hotbar;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;


@Mixin(HotbarManager.class)
public class HotbarManagerMixin {
    @Mutable
    @Shadow @Final private Hotbar[] hotbars;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        hotbars = Arrays.stream(hotbars).limit(8).toArray(Hotbar[]::new);
    }

    @ModifyConstant(method = "load", constant = @Constant(intValue = 9))
    private int load(int constant) {
        return 8;
    }

    @ModifyConstant(method = "save", constant = @Constant(intValue = 9))
    private int save(int constant) {
        return 8;
    }
}
