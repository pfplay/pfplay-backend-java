package com.pfplaybackend.api.common;

import com.pfplaybackend.api.common.aspect.context.AuthContext;

public class ThreadLocalContext {
    private static final ThreadLocal<Object> contextHolder = new ThreadLocal<>();

    public static void setContext(Object object) {
        contextHolder.set(object);
    }

    public static Object getContext() {
        return contextHolder.get();
    }

    public static AuthContext getAuthContext() {
        Object context = getContext();
        if (context instanceof AuthContext authContext) {
            return authContext;
        }
        throw new IllegalStateException("AuthContext not available in current thread");
    }

    public static void clearContext() {
        contextHolder .remove();
    }
}