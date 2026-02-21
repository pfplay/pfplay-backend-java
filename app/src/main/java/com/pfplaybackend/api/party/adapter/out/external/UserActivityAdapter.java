package com.pfplaybackend.api.party.adapter.out.external;

import com.pfplaybackend.api.party.application.port.out.UserActivityPort;
import com.pfplaybackend.api.user.application.service.UserActivityCommandService;
import com.pfplaybackend.api.common.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserActivityAdapter implements UserActivityPort {
    private final UserActivityCommandService userActivityCommandService;

    @Override
    public void updateDjPointScore(UserId userId, int score) {
        userActivityCommandService.updateDjPointScore(userId, score);
    }
}
