package com.eteryun.modules;

public interface IModule {
    String name();
    void preInit();
    void init();
    void shutdown();
}
