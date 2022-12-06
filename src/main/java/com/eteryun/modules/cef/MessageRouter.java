package com.eteryun.modules.cef;

import com.eteryun.modules.cef.query.QueryData;
import com.eteryun.modules.cef.query.QueryTarget;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MessageRouter extends CefMessageRouterHandlerAdapter {
    private final Map<String, QueryData> REGISTRY_MAP = new HashMap<>();

    private boolean isMethodBad(final Method method) {
        return !method.isAnnotationPresent(QueryTarget.class);
    }

    public void unregister(final Object o, final String name) {
        if (REGISTRY_MAP.containsKey(name)) {
            QueryData methodData = REGISTRY_MAP.get(name);
            if (methodData.source.equals(o)) {
                REGISTRY_MAP.remove(name);
            }
        }
    }

    public void unregister(final Object o) {
        for (Map.Entry<String, QueryData> eventDataEntry : REGISTRY_MAP.entrySet()) {
            if (eventDataEntry.getValue().source.equals(o)) {
                REGISTRY_MAP.remove(eventDataEntry.getKey());
            }
        }
    }

    public void register(final Method method, final Object o) {
        final QueryData methodData = new QueryData(o, method);
        QueryTarget target = method.getAnnotation(QueryTarget.class);

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

    @Override
    public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        JsonObject jsonObject = new Gson().fromJson(request, JsonObject.class);

        QueryData messageData = REGISTRY_MAP.get(GsonHelper.getAsString(jsonObject, "name"));

        Object data = "";

        try {
            data = GsonHelper.getAsString(jsonObject, "data");
        } catch (Exception e) {
            data = GsonHelper.getAsJsonObject(jsonObject, "data");
        }


        if (messageData != null) {
            try {
                Object result = messageData.target.invoke(messageData.source, data);
                callback.success(result == null ? "{}" : String.valueOf(result));
                return true;
            } catch (IllegalAccessException | InvocationTargetException ex) {
                ex.printStackTrace();
            }
        }

        return super.onQuery(browser, frame, queryId, request, persistent, callback);
    }
}
