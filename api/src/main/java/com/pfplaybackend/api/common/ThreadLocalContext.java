package com.pfplaybackend.api.common;

public class ThreadLocalContext {
    private static final ThreadLocal<Object> contextHolder = new ThreadLocal<>();

    public static void setContext(Object object) {
        contextHolder.set(object);
    }

    public static Object getContext() {
        return contextHolder.get();
    }

    public static void clearContext() {
        contextHolder .remove();
    }
}