package com.eteryun.modules.backtool.mixin.packs;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Mixin(VanillaPackResources.class)
public class VanillaPackResourcesMixin {
    @Shadow
    private Set<String> namespaces;

    @Inject(method = "getNamespaces", at = @At("HEAD"), cancellable = true)
    private void getNamespaces(PackType packType, CallbackInfoReturnable<Set<String>> cir){
        Set<String> newNamespaces = new HashSet<>();
        newNamespaces.addAll(this.namespaces);
        newNamespaces.add("eteryun");
        cir.setReturnValue(newNamespaces);
    }
}
