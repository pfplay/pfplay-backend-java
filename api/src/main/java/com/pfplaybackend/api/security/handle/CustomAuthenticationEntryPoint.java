package com.pfplaybackend.api.security.handle;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.ExceptionResult;
import com.pfplaybackend.api.config.ObjectMapperConfig;
import com.pfplaybackend.api.enums.ExceptionEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;


@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {


    private ObjectMapperConfig om;

    public CustomAuthenticationEntryPoint(ObjectMapperConfig objectMapperConfig) {
        this.om = objectMapperConfig;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        OutputStream responseStream = response.getOutputStream();
        om.mapper().writeValue(
                responseStream,
                ApiCommonResponse.error(
                    ExceptionResult.builder()
                            .status(HttpStatus.UNAUTHORIZED)
                            .code(ExceptionEnum.UNAUTHORIZED.getHttpStatusCode())
                            .message(ExceptionEnum.UNAUTHORIZED.getMessage())
                            .stackTrace(authException.getMessage())
                            .build()
                    )
                );
        responseStream.flush();
    }
}
