package com.eteryun.ui.events;

import java.lang.reflect.Method;

public class UIEventData {
    public final Object source;
    public final Method target;

    public UIEventData(Object source, Method target) {
        this.source = source;
        this.target = target;
    }
}