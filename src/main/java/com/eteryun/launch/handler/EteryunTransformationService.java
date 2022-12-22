package com.eteryun.launch.handler;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EteryunTransformationService implements ITransformationService {
    @NotNull
    @Override
    public String name() {
        return "eteryuntransformer";
    }

    @Override
    public void initialize(IEnvironment environment) {
        MixinBootstrap.init();
    }

    @Override
    public void beginScanning(IEnvironment environment) {
        Mixins.addConfiguration("eteryun.mixins.json");
        Mixins.addConfiguration("cef.mixins.json");
        Mixins.addConfiguration("playerscale.mixins.json");
        Mixins.addConfiguration("backtoo.mixins.json");
        Mixins.addConfiguration("ui.mixins.json");

        final MixinEnvironment env = MixinEnvironment.getDefaultEnvironment();

        System.out.println(env.getObfuscationContext());

        if (env.getObfuscationContext() == null) {
            env.setObfuscationContext("named:official");
        }

    }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) throws IncompatibleEnvironmentException {

    }

    @NotNull
    @Override
    public List<ITransformer> transformers() {
        return new ArrayList<>();
    }
}
