package com.pfplaybackend.api.security.handle;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("fail request info getProtocol={}", request.getProtocol());
        log.info("fail request info getPathInfo={}", request.getPathInfo());
        log.info("fail request info getRemoteHost={}", request.getRemoteHost());
        log.info("fail request info getServerPort={}", request.getServerPort());
        log.info("fail request info getLocalPort={}", request.getLocalPort());
        log.info("fail request info getRequestURI={}", request.getRequestURI());
        log.info("fail request info getRemoteAddr={}", request.getRemoteAddr());

        response.sendRedirect("/error");
    }
}
