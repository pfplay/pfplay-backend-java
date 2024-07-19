package com.pfplaybackend.api.config.setup;

import com.pfplaybackend.api.partyroom.application.service.initialize.PartyroomInitializeService;
import com.pfplaybackend.api.user.application.service.initialize.AdminUserInitializeService;
import com.pfplaybackend.api.user.application.service.initialize.AvatarResourceInitializeService;
import com.pfplaybackend.api.user.application.service.initialize.TemporaryUserInitializeService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationReadyEventListener {

    private final Environment environment;
    private final AvatarResourceInitializeService avatarResourceInitializeService;
    private final TemporaryUserInitializeService temporaryUserInitializeService;
    private final AdminUserInitializeService adminUserInitializeService;
    private final PartyroomInitializeService partyroomInitializeService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isLocalProfileActive = false;

        for (String profile : activeProfiles) {
            if ("local".equals(profile)) {
                isLocalProfileActive = true;
                break;
            }
        }

        // TODO Prod 이상 환경에서는 동작 '불필요'
        // Add AdminUser
        adminUserInitializeService.addAdminUser();
        // Add AvatarResources
        avatarResourceInitializeService.addAvatarBodies();
        avatarResourceInitializeService.addAvatarFaces();
        avatarResourceInitializeService.addAvatarIcons();
        // FIXME 서비스 간 '구동 순서에 대한 의존 문제'를 해소
        // Add Main Partyroom
        partyroomInitializeService.addPartyroomByAdmin();

        if (isLocalProfileActive) {
            System.out.println("Local profile is active");
            temporaryUserInitializeService.addTemporaryUsers();
        } else {
            System.out.println("Local profile is not active");
        }
    }
}