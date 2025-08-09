package com.pfplaybackend.api.auth.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthCleanupScheduler {

    private final OAuthUrlService oAuthUrlService;

    /**
     * 만료된 OAuth state 정리 (매 5분마다 실행)
     */
    @Scheduled(fixedRate = 300_000) // 5분 = 300,000ms
    public void cleanupExpiredStates() {
        try {
            log.debug("Starting OAuth state cleanup...");
            oAuthUrlService.cleanupExpiredStates();
            log.debug("OAuth state cleanup completed");
        } catch (Exception e) {
            log.error("Failed to cleanup expired OAuth states: {}", e.getMessage(), e);
        }
    }
}
