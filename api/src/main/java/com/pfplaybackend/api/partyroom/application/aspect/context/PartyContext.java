package com.pfplaybackend.api.partyroom.application.aspect.context;

import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import org.springframework.security.core.Authentication;

public class PartyContext {
    private static final ThreadLocal<UserCredentials> userThreadLocal = new ThreadLocal<>();

    public static void setUserCredentials(Authentication authentication) {
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