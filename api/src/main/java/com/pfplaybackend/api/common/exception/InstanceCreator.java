package com.pfplaybackend.api.common.exception;

import java.lang.reflect.Constructor;

public class InstanceCreator {

    @SuppressWarnings("unchecked")
    public static <T> T createInstance(Class<? extends T> clazz, Object... args) {
        try {
            // 기본 생성자를 호출하여 객체 생성
            Constructor<? extends T> constructor = (Constructor<? extends T>) clazz.getDeclaredConstructors()[0];
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance", e);
        }
    }
}