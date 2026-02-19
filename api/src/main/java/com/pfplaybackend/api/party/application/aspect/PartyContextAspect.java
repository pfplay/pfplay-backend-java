package com.pfplaybackend.api.party.application.aspect;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.config.security.jwt.CustomJwtAuthenticationToken;
import com.pfplaybackend.api.party.application.aspect.context.PartyContext;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PartyContextAspect {

    @Pointcut("execution(* com.pfplaybackend.api.party.application.service.*.*(..))")
    public void contextRequiredMethods() {}

    @Around("contextRequiredMethods()")
    public Object aroundServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        boolean isOutermost = ThreadLocalContext.getContext() == null;
        if (isOutermost) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && !authentication.getPrincipal().equals("anonymousUser")) {
                PartyContext partyContext = PartyContext.create((CustomJwtAuthenticationToken) authentication);
                ThreadLocalContext.setContext(partyContext);
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