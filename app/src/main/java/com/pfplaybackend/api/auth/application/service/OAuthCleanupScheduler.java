package com.pfplaybackend.api.auth.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Redis TTL이 OAuth state 만료를 자동 처리하므로
 * 별도의 cleanup 스케줄러가 더 이상 필요하지 않습니다.
 */
@Slf4j
@Component
public class OAuthCleanupScheduler {
}
