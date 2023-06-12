package com.pfplaybackend.api.security.handle;

import com.pfplaybackend.api.common.ApiResponse;
import com.pfplaybackend.api.common.ResponseMessage;
import com.pfplaybackend.api.config.ObjectMapperConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapperConfig objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapperConfig objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String json = objectMapper.mapper().writeValueAsString(
                ApiResponse.error(ResponseMessage.make(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.name()))
        );
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}