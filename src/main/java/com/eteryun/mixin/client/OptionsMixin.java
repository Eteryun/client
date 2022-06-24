package com.eteryun.mixin.client;

import com.eteryun.KeyMappingsRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public class OptionsMixin {
    @Mutable
    @Final
    @Shadow
    public KeyMapping[] keyMappings;

    @Inject(method = "load", at = @At("HEAD"))
    public void load(CallbackInfo ci) {
        this.keyMappings = KeyMappingsRegistry.process(this.keyMappings);
    }
}
