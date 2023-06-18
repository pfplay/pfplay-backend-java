package com.pfplaybackend.api.security.handle;


import jakarta.servlet.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("filter request = {} ", request);
        log.info("filter response = {} ", response);
        chain.doFilter(request, response);
    }
}
