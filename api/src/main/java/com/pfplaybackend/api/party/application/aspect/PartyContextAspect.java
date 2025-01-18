package com.pfplaybackend.api.party.application.aspect;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import com.pfplaybackend.api.party.application.aspect.context.PartyContext;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
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

    @Before("contextRequiredMethods()")
    public void beforeServiceMethods(JoinPoint joinPoint) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && !authentication.getPrincipal().equals("anonymousUser")) {
            PartyContext partyContext = PartyContext.create((UserCredentials)authentication.getPrincipal());
            ThreadLocalContext.setContext(partyContext);
        }
    }

    @After("contextRequiredMethods()")
    public void clearContext() {
        ThreadLocalContext.clearContext();
    }
}