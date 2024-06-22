package com.pfplaybackend.api.user.event.listener;

import com.pfplaybackend.api.user.application.service.AdminUserService;
import com.pfplaybackend.api.user.application.service.temporary.TemporaryAvatarResourceService;
import com.pfplaybackend.api.user.application.service.temporary.TemporaryUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationReadyEventListener {

    private final Environment environment;
    private final AdminUserService adminUserService;
    private final TemporaryAvatarResourceService temporaryAvatarResourceService;
    private final TemporaryUserService temporaryUserService;

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

        if (isLocalProfileActive) {
            System.out.println("Local profile is active");
            temporaryAvatarResourceService.addTemporaryAvatarBodies();
            adminUserService.addAdminUser();
            temporaryUserService.addTemporaryUsers();
        } else {
            System.out.println("Local profile is not active");
        }
    }
}
