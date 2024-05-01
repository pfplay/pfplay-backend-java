package com.pfplaybackend.api.user.model.domain;

import com.pfplaybackend.api.user.model.enums.AuthorityTier;
import com.pfplaybackend.api.user.model.value.UserId;
import lombok.Getter;

import java.util.Objects;

@Getter
public abstract class UserDomain {

    protected UserId userId;
    protected AuthorityTier authorityTier;
    // TODO Guest 객체도 UserProfileDomain, UserActivityDomain 을 가질 수 있는가?
    // TODO 아래의 두 도메인 객체의 식별자가 지정되지 않은 상태에서 도메인 객체를 할당하고
    // TODO toEntity() 를 호출하는 과정에서 엔티티로 변환해서 할당하는 방식은 어떠한가?
    // TODO 만약 위 방식이 가능하다고 가정할 때 @ManyToOne, @OneToMany 관계에도 모두 적용 가능한가?
    // TODO 최초 생성 시나리오가 아닌 경우 Domain to Entity 가 아예 불가능하다. 변경 대상 Entity 를 미리 조회해야만 가능하기 때문이다.
    protected com.pfplaybackend.api.user.model.domain.ProfileDomain ProfileDomain;
    protected com.pfplaybackend.api.user.model.domain.ActivityDomain ActivityDomain;

    UserDomain() {
        this.userId = new UserId();
    }

    UserDomain(AuthorityTier authorityTier) {
        this.userId = new UserId();
        this.authorityTier = authorityTier;
    }

    UserDomain(UserId userId, AuthorityTier authorityTier) {
        this.userId = Objects.requireNonNullElseGet(userId, UserId::new);
        this.authorityTier = authorityTier;
    }
}

