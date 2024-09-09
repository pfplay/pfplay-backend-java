package com.pfplaybackend.api.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.exception.http.AbstractHTTPException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        }
        catch (AbstractHTTPException e) {
            createdExceptionResponse(response, e);
        }
    }

    private void createdExceptionResponse(HttpServletResponse response, AbstractHTTPException e) {
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
}
