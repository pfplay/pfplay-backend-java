package com.pfplaybackend.api.user.model.domain;

import com.pfplaybackend.api.user.model.domain.operations.GuestOperation;
import com.pfplaybackend.api.user.model.entity.Guest;
import com.pfplaybackend.api.user.model.enums.AuthorityTier;
import com.pfplaybackend.api.user.model.value.UserId;
import lombok.Builder;

import java.io.Serializable;

public class GuestDomain extends UserDomain implements GuestOperation {

    final private String agent;

    public GuestDomain(String agent) {
        this.agent = agent;
    }

    @Builder
    public GuestDomain(UserId uid, String agent) {
        super(uid, AuthorityTier.GT);
        this.agent = agent;
    }

    public Guest toEntity() {
        return Guest.builder()
                .userId(this.userId)
                .agent(this.agent)
                .authorityTier(this.authorityTier)
                .build();
    }

    static public GuestDomain create(String agent) {
        return new GuestDomain(agent);
    }

    @Override
    public MemberDomain updateProfile(MemberDomain memberDomain) {
        return null;
    }
}
