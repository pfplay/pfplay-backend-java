package com.pfplaybackend.api.config.jwt;

import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.config.jwt.exception.JwtAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) {
        throw ExceptionCreator.create(JwtAuthenticationException.ACCESS_TOKEN_NOT_FOUND);
    }
}
