package com.eteryun.mixin.packs;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashSet;
import java.util.Set;

@Mixin(VanillaPackResources.class)
public class VanillaPackResourcesMixins {
    @Shadow
    private Set<String> namespaces;

    /**
     * @author eteryun
     * @reason
     */
    @Overwrite
    public Set<String> getNamespaces(PackType pType)
    {
        Set<String> newNamespaces = new HashSet<>();
        newNamespaces.addAll(this.namespaces);
        newNamespaces.add("eteryun");
        return newNamespaces;
    }
}
