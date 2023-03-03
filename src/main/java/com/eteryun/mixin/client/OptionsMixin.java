package com.eteryun.mixin.client;

import com.eteryun.utils.KeyMappingsHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.stream.Stream;

@Mixin(Options.class)
public class OptionsMixin {
    @Mutable
    @Final
    @Shadow
    public KeyMapping[] keyMappings;

    @Shadow
    public boolean autoJump;

    @Shadow
    public int guiScale;

    @Mutable
    @Shadow
    @Final
    public KeyMapping keySaveHotbarActivator;

    @Mutable
    @Shadow
    @Final
    public KeyMapping keyLoadHotbarActivator;

    @Mutable
    @Shadow
    @Final
    public KeyMapping[] keyHotbarSlots;

    @Inject(method = "load", at = @At("HEAD"))
    public void load(CallbackInfo ci) {
        keySaveHotbarActivator = new KeyMapping("key.saveToolbarActivator", GLFW.GLFW_KEY_KP_0, "key.categories.creative");
        keyLoadHotbarActivator = new KeyMapping("key.loadToolbarActivator", GLFW.GLFW_KEY_KP_1, "key.categories.creative");
        keyHotbarSlots = Arrays.stream(keyHotbarSlots).limit(8).toArray(KeyMapping[]::new);
        Stream<KeyMapping> keyMappingStream = Arrays.stream(this.keyMappings);
        keyMappingStream = keyMappingStream.filter(keyMapping -> !keyMapping.getName().startsWith("key.hotbar") && !keyMapping.getName().equals("key.saveToolbarActivator") && !keyMapping.getName().equals("key.loadToolbarActivator"));
        this.keyMappings = ArrayUtils.addAll(keyMappingStream.toArray(KeyMapping[]::new), keyHotbarSlots);
        this.keyMappings = KeyMappingsHelper.process(this.keyMappings);
    }

    @Inject(method = "processOptions", at = @At("RETURN"))
    public void processOptionsMod(CallbackInfo ci) {
        this.autoJump = false;
        this.guiScale = 2;
    }
}
