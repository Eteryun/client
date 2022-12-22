package com.eteryun.launch.handler;

import cpw.mods.gross.Java9ClassLoaderUtil;
import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import cpw.mods.modlauncher.api.ITransformingClassLoaderBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

public class EteryunLaunchService implements ILaunchHandlerService {
    private static final Logger logger = LogManager.getLogger("Eteryun");

    @Override
    public String name() {
        return "eteryunlaunch";
    }

    @Override
    public void configureTransformationClassLoader(ITransformingClassLoaderBuilder builder) {
        for (final URL url : Java9ClassLoaderUtil.getSystemClassPathURLs()) {
            try {
                if (url.toString().startsWith("org.slf4j")) {
                    this.logger.debug("Skipped adding transformation path for '" + url + "'!");
                    continue;
                }

                builder.addTransformationPath(Paths.get(url.toURI()));
                this.logger.debug("Added transformation path for '" + url + "'");
            } catch (final URISyntaxException exception) {
                this.logger.error("Failed to add transformation path for '" + url + "'!", exception);
            }
        }
    }

    @Override
    public Callable<Void> launchService(String[] arguments, ITransformingClassLoader launchClassLoader) {
        return () -> {
            try {
                Class.forName("net.minecraft.client.main.Main", true, launchClassLoader.getInstance())
                        .getMethod("main", String[].class)
                        .invoke(null,  (Object) arguments);
            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        };
    }
}
