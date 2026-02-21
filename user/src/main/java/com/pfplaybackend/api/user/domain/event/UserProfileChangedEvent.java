package com.pfplaybackend.api.user.domain.event;

import com.pfplaybackend.api.common.domain.event.DomainEvent;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.user.domain.enums.ProfileChangeType;
import lombok.Getter;

@Getter
public class UserProfileChangedEvent extends DomainEvent {
    private final UserId userId;
    private final ProfileChangeType changeType;

    public UserProfileChangedEvent(UserId userId, ProfileChangeType changeType) {
        this.userId = userId;
        this.changeType = changeType;
    }

    @Override
    public String getAggregateId() {
        return userId.getUid().toString();
    }
}
