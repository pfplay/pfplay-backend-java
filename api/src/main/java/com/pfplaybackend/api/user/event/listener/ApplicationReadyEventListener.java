package com.pfplaybackend.api.user.event.listener;

import com.pfplaybackend.api.user.application.service.initialize.AdminUserInitializeService;
import com.pfplaybackend.api.user.application.service.initialize.AvatarResourceInitializeService;
import com.pfplaybackend.api.user.application.service.initialize.TemporaryUserInitializeService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
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
        // Add AdminUser
        adminUserInitializeService.addAdminUser();
        // Add AvatarResources
        avatarResourceInitializeService.addAvatarBodies();
        avatarResourceInitializeService.addAvatarFaces();
        avatarResourceInitializeService.addAvatarIcons();

        if (isLocalProfileActive) {
            System.out.println("Local profile is active");
            temporaryUserInitializeService.addTemporaryUsers();
        } else {
            System.out.println("Local profile is not active");
        }
    }
}
