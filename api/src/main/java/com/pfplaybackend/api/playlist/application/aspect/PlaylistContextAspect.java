package com.pfplaybackend.api.playlist.application.aspect;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.config.security.jwt.CustomJwtAuthenticationToken;
import com.pfplaybackend.api.playlist.application.aspect.context.PlaylistContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PlaylistContextAspect {
    @Pointcut("execution(* com.pfplaybackend.api.playlist.application.service.*.*(..))")
    public void contextRequiredMethods() {}

    @Around("contextRequiredMethods()")
    public Object aroundServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        boolean isOutermost = ThreadLocalContext.getContext() == null;
        if (isOutermost) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && !authentication.getPrincipal().equals("anonymousUser")) {
                PlaylistContext playlistContext = PlaylistContext.create((CustomJwtAuthenticationToken) authentication);
                ThreadLocalContext.setContext(playlistContext);
            }
        }
        try {
            return joinPoint.proceed();
        } finally {
            if (isOutermost) {
                ThreadLocalContext.clearContext();
            }
        }
    }
}
