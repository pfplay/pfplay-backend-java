package com.pfplaybackend.api.user.domain.event;

import com.pfplaybackend.api.common.domain.event.DomainEvent;
import com.pfplaybackend.api.common.domain.value.UserId;
import lombok.Getter;

@Getter
public class UserProfileChangedEvent extends DomainEvent {
    private final UserId userId;

    public UserProfileChangedEvent(UserId userId) {
        this.userId = userId;
    }

    @Override
    public String getAggregateId() {
        return userId.getUid().toString();
    }
}
