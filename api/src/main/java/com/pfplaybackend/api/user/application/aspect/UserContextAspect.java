package com.pfplaybackend.api.user.application.aspect;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
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
    public void contextRequiredMethods() {}

    @Before("contextRequiredMethods()")
    public void beforeServiceMethods(JoinPoint joinPoint) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && !authentication.getPrincipal().equals("anonymousUser")) {
            UserContext userContext = UserContext.create((UserCredentials)authentication.getPrincipal());
            ThreadLocalContext.setContext(userContext);
        }
    }

    @After("contextRequiredMethods()")
    public void clearContext() {
        ThreadLocalContext.clearContext();
    }
}
