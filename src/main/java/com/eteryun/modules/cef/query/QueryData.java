package com.eteryun.modules.cef.query;

import java.lang.reflect.Method;

public class QueryData {
    public final Object source;
    public final Method target;

    public QueryData(Object source, Method target) {
        this.source = source;
        this.target = target;
    }
}
