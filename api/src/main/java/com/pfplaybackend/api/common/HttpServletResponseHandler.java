package com.pfplaybackend.api.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.common.exception.DomainException;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.exception.ExceptionResult;
import com.pfplaybackend.api.common.exception.SecurityException;
import com.pfplaybackend.api.common.exception.http.AbstractHTTPException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.OutputStream;

@Slf4j
public class HttpServletResponseHandler {
    private static void HttpExceptionToHttpResponse(HttpServletResponse response, AbstractHTTPException e) {
        log.error(e.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(e.getStatus().value());
        try (OutputStream os = response.getOutputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(os, ApiCommonResponse.error(
                            ExceptionResult.builder()
                                    .status(e.getStatus())
                                    .code(e.getStatus().value())
                                    .message(e.getMessage())
                                    .errorCode(e.getErrorCode())
                                    .build()
                    )
            );
            os.flush();
        } catch (IOException ioException) {
            throw new RuntimeException("Failed to create ApiCommonResponse", ioException);
        }
    }

    public static void setByException(HttpServletResponse response, DomainException domainException) {
        HttpExceptionToHttpResponse(response, ExceptionCreator.create(domainException));
    }

    public static void setByException(HttpServletResponse response, SecurityException securityException) {
        HttpExceptionToHttpResponse(response, ExceptionCreator.create(securityException));
    }
}
