package com.eteryun.launch;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EteryunTweaker implements ITweaker {

    private static final String MAIN_CLASS = "net.minecraft.client.main.Main";

    private List<String> args = new ArrayList<>();

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String version) {
        this.args.addAll(args);

        if (gameDir != null) {
            this.args.add("--gameDir");
            this.args.add(gameDir.getPath());
        }

        if (assetsDir != null) {
            this.args.add("--assetsDir");
            this.args.add(assetsDir.toString());
        }

        if (version != null) {
            this.args.add("--version");
            this.args.add(version);
        }
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        classLoader.addClassLoaderExclusion("org.slf4j");

        MixinBootstrap.init();

        Mixins.addConfiguration("eteryun.mixins.json");
        Mixins.addConfiguration("cef.mixins.json");
        Mixins.addConfiguration("playerscale.mixins.json");
        Mixins.addConfiguration("backtool.mixins.json");

        final MixinEnvironment environment = MixinEnvironment.getDefaultEnvironment();

        if (environment.getObfuscationContext() == null) {
            environment.setObfuscationContext("named:official");
        }

        environment.setSide(MixinEnvironment.Side.CLIENT);
    }

    @Override
    public String getLaunchTarget() {
        return MAIN_CLASS;
    }

    @Override
    public String[] getLaunchArguments() {
        return args.toArray(String[]::new);
    }
}
