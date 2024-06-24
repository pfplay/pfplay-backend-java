package com.pfplaybackend.api.partyroom.application.aspect;

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
public class PartyContextAspect {
    @Pointcut("execution(* com.pfplaybackend.api.partyroom.application.service.*.*(..))")
    public void partyContextRequiredMethods() {}

    @Before("partyContextRequiredMethods()")
    public void beforeServiceMethods(JoinPoint joinPoint) {

        // TODO Query to REDIS
        // TODO 조회 실패하면 Query RDB

        // 1. Caller(클라이언트)가 타겟으로 요청한 파티룸의 파티원이 맞는지
        // 2. 나의 '멤버 레벨'을 PartyContext 에 주입
        // 3. (파티멤버 레귤레이션)나 뿐만 아니라 타겟 멤버와 관련된 필드가 있다면 그 녀석의 멤버 레벨도 PartyContext에 주입합니다.



//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && !authentication.getPrincipal().equals("anonymousUser")) {
//            UserContext.setUserCredentials(authentication);
//        }

    }

    @After("partyContextRequiredMethods()")
    public void clearUserContext() {
        // UserContext.clear();
    }
}
