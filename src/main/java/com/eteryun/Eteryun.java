package com.eteryun;

import com.eteryun.event.EventManager;
import com.eteryun.network.PacketsProtocol;
import com.ramon.ultralight.UltralightEngine;
import net.minecraft.client.KeyMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

public class Eteryun {
    private static final Logger logger = LogManager.getLogger("Eteryun");

    private static final Eteryun instance = new Eteryun();

    public static final KeyMapping keyBackTool = new KeyMapping("et.swap.backtool", GLFW.GLFW_KEY_G, KeyMapping.CATEGORY_INVENTORY);

    public void init() {
        logger.info("Inicializando cliente");
        KeyMappingsRegistry.registerKeyMapping(keyBackTool);
    }

    public void start() {
        logger.info("Iniciando cliente");
        new UltralightEngine();
        PacketsProtocol.registerPackets();


        EventManager.register(new Events());
    }

    public void shutdown() {
        logger.info("Desligando cliente");
    }

    public static Eteryun getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return logger;
    }
}
