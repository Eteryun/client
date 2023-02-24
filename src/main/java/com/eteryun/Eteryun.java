package com.eteryun;

import com.eteryun.modules.IModule;
import com.eteryun.modules.backtoo.BacktooModule;
import com.eteryun.modules.cef.CefManager;
import com.eteryun.modules.ui.UiModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class Eteryun {
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
        modules.add(new CefManager());
        modules.add(new BacktooModule());
        modules.add(new UiModule());
    }
}
