package com.pfplaybackend.api.party.application.proxy;

import com.pfplaybackend.api.party.application.peer.UserActivityPeerService;
import com.pfplaybackend.api.user.application.service.UserActivityService;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserActivityProxyService implements UserActivityPeerService {
    private final UserActivityService userActivityService;

    @Override
    public void updateDjPointScore(UserId userId, int score) {
        userActivityService.updateDjPointScore(userId, score);
    }
}
