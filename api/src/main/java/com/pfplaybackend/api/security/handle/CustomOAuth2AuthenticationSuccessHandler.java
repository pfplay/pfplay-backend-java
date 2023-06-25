package com.pfplaybackend.api.security.handle;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("success request info getProtocol={}", request.getProtocol());
        log.info("success request info getPathInfo={}", request.getPathInfo());
        log.info("success request info getRemoteHost={}", request.getRemoteHost());
        log.info("success request info getServerPort={}", request.getServerPort());
        log.info("success request info getLocalPort={}", request.getLocalPort());
        log.info("success request info getRequestURI={}", request.getRequestURI());
        log.info("success request info getRemoteAddr={}", request.getRemoteAddr());
        response.sendRedirect( "/api/v1/user/join");
    }
}
