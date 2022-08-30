package com.eteryun.ui.events;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class UIEventManager {
    private static final Map<String, UIEventData> REGISTRY_MAP = new HashMap<>();
    private static UIEventManager instance;

    static {
        instance = new UIEventManager();
    }

    public static UIEventManager getInstance() {
        return instance;
    }

    private boolean isMethodBad(final Method method) {
        return !method.isAnnotationPresent(UIEventTarget.class);
    }

    public UIEventData get(final String name) {
        return REGISTRY_MAP.get(name);
    }

    public void unregister(final Object o, final String name) {
        if (REGISTRY_MAP.containsKey(name)) {
            UIEventData methodData = REGISTRY_MAP.get(name);
            if (methodData.source.equals(o)) {
                REGISTRY_MAP.remove(name);
            }
        }
    }

    public void unregister(final Object o) {
        for (Map.Entry<String, UIEventData> eventDataEntry : REGISTRY_MAP.entrySet()) {
            if (eventDataEntry.getValue().source.equals(o)) {
                REGISTRY_MAP.remove(eventDataEntry.getKey());
            }
        }
    }

    public void register(final Method method, final Object o) {
        final UIEventData methodData = new UIEventData(o, method);
        UIEventTarget target = method.getAnnotation(UIEventTarget.class);

        if (methodData.target.trySetAccessible()) {
            methodData.target.setAccessible(true);
        }

        REGISTRY_MAP.put(target.name(), methodData);
    }

    public void register(final Object o) {
        for (final Method method : o.getClass().getMethods()) {
            if (!isMethodBad(method)) {
                register(method, o);
            }
        }
    }

    public Object call(String name, String body) {
        Object object = null;
        try {
            object = new Gson().fromJson(body, JsonObject.class);
        } catch (Exception e) {
            object = body;
        }

        final UIEventData eventData = UIEventManager.getInstance().get(name);

        if (eventData == null) {
            return false;
        }

        try {
            return eventData.target.invoke(eventData.source, object);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
