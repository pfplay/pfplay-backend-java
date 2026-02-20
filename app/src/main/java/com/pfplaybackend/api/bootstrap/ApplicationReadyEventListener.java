package com.pfplaybackend.api.bootstrap;

import com.pfplaybackend.api.party.application.service.PartyroomManagementService;
import com.pfplaybackend.api.user.application.service.initialize.AdminUserInitializeService;
import com.pfplaybackend.api.user.application.service.initialize.AvatarResourceInitializeService;
import com.pfplaybackend.api.user.application.service.initialize.TemporaryUserInitializeService;
import com.pfplaybackend.api.common.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationReadyEventListener {

    private final Environment environment;
    private final AvatarResourceInitializeService avatarResourceInitializeService;
    private final TemporaryUserInitializeService temporaryUserInitializeService;
    private final AdminUserInitializeService adminUserInitializeService;
    private final PartyroomManagementService partyroomManagementService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {
        if (!environment.acceptsProfiles(Profiles.of("prod"))) {
            avatarResourceInitializeService.addAvatarBodies();
            avatarResourceInitializeService.addAvatarFaces();
            avatarResourceInitializeService.addAvatarIcons();
        }
        // FIXME 서비스 간 '구동 순서에 대한 의존 문제'를 해소
        // Add AdminUser
        UserId adminId = adminUserInitializeService.addAdminUser();
        // Add Main Partyroom
        partyroomManagementService.initializeMainStage(adminId);

        if (environment.acceptsProfiles(Profiles.of("local"))) {
            temporaryUserInitializeService.addTemporaryUsers();
        }
    }
}
