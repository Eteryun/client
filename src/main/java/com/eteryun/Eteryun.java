package com.eteryun;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Eteryun {
    private static final Logger logger = LogManager.getLogger("Eteryun");

    private static final Eteryun instance = new Eteryun();

    public void init() {
        logger.info("Inicializando cliente");
    }

    public void start() {
        logger.info("Iniciando cliente");
    }

    public void shutdown() {
        logger.info("Desligando cliente");
    }

    public static Eteryun getInstance() {
        return instance;
    }

    public static Logger getLogger(){
        return logger;
    }
}
