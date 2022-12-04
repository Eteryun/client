package com.eteryun;

import com.eteryun.modules.IModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import com.eteryun.modules.Module;

import java.util.ArrayList;
import java.util.Set;

public class Eteryun {
    private Reflections reflections = new Reflections("com.eteryun.modules");
    private static ArrayList<IModule> modules = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger("Eteryun");

    private static final Eteryun instance = new Eteryun();

    public void preInit() {
        logger.info("Inicializando cliente");
        modules.forEach(IModule::preInit);
    }

    public void init() {
        logger.info("Iniciando cliente");
        modules.forEach(IModule::init);
    }

    public void shutdown() {
        logger.info("Desligando cliente");
        modules.forEach(IModule::shutdown);
    }

    public static Eteryun getInstance() {
        return instance;
    }

    public static Logger getLogger(){
        return logger;
    }

    public void loadModules() {
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Module.class);
        annotated.forEach(pClass -> {
            try {
                modules.add((IModule) pClass.newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }
}
