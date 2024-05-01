package com.pfplaybackend.api.user.model.entity;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.user.model.domain.GuestDomain;
import com.pfplaybackend.api.user.model.enums.AuthorityTier;
import com.pfplaybackend.api.user.model.value.UserId;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@DynamicUpdate
@DynamicInsert
@Table(name = "GUEST")
@Entity
public class Guest extends BaseEntity {

    @EmbeddedId
    private UserId userId;

    private String agent;

    @OneToOne
    private Profile profile;

    private boolean isProfileUpdated;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthorityTier authorityTier;

    public Guest() { }

    @Builder
    public Guest(UserId userId, String agent, AuthorityTier authorityTier) {
        this.userId = userId;
        this.agent = agent;
        this.authorityTier = authorityTier;
    }

    public GuestDomain toDomain() {
        return GuestDomain.builder()
                .uid(this.userId)
                .agent(this.agent)
                .build();
    }
}
