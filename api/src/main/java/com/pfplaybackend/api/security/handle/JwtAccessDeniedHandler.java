package com.pfplaybackend.api.security.handle;

import com.pfplaybackend.api.common.ApiResponse;
import com.pfplaybackend.api.common.ResponseMessage;
import com.pfplaybackend.api.config.ObjectMapperConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapperConfig objectMapper;

    public JwtAccessDeniedHandler(ObjectMapperConfig objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException, IOException {
        String json = objectMapper.mapper().writeValueAsString(
                ApiResponse.error(ResponseMessage.make(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.name()))
        );
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}



