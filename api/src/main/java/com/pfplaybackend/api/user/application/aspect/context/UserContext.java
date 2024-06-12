package com.pfplaybackend.api.user.application.aspect.context;

import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import org.springframework.security.core.Authentication;

public class UserContext {
    private static final ThreadLocal<UserCredentials> userThreadLocal = new ThreadLocal<>();

    public static void setUserCredentials(Authentication authentication) {
        System.out.println(authentication);
        UserCredentials userCredentials = (UserCredentials) authentication.getPrincipal();
        userThreadLocal.set(userCredentials);
    }

    public static UserCredentials getUserCredentials() {
        return userThreadLocal.get();
    }

    public static void clear() {
        userThreadLocal.remove();
    }
}
