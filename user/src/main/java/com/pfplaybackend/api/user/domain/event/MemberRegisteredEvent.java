package com.pfplaybackend.api.user.domain.event;

import com.pfplaybackend.api.common.config.security.enums.ProviderType;
import com.pfplaybackend.api.common.domain.event.DomainEvent;
import com.pfplaybackend.api.common.domain.value.UserId;
import lombok.Getter;

@Getter
public class MemberRegisteredEvent extends DomainEvent {
    private final UserId userId;
    private final String email;
    private final ProviderType providerType;

    public MemberRegisteredEvent(UserId userId, String email, ProviderType providerType) {
        this.userId = userId;
        this.email = email;
        this.providerType = providerType;
    }

    @Override
    public String getAggregateId() {
        return userId.getUid().toString();
    }
}
