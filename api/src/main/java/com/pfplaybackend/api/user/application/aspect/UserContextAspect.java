package com.pfplaybackend.api.user.application.aspect;

import com.pfplaybackend.api.user.application.aspect.context.UserContext;
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
public class UserContextAspect {
    @Pointcut("execution(* com.pfplaybackend.api.user.application.service.*.*(..))")
    public void userContextRequiredMethods() {}

    @Before("userContextRequiredMethods()")
    public void beforeServiceMethods(JoinPoint joinPoint) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !authentication.getPrincipal().equals("anonymousUser")) {
            UserContext.setUserCredentials(authentication);
        }
    }

    @After("userContextRequiredMethods()")
    public void clearUserContext() {
        UserContext.clear();
    }
}
