package com.pfplaybackend.api.user.adapter.out.persistence.impl;

import com.pfplaybackend.api.profile.domain.QProfileData;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.entity.data.QActivityData;
import com.pfplaybackend.api.user.domain.entity.data.QMemberData;
import com.pfplaybackend.api.user.domain.value.UserId;
import com.pfplaybackend.api.user.adapter.out.persistence.custom.MemberRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<MemberData> findByUserId(UserId userId) {
        QMemberData qMemberData = QMemberData.memberData;
        QProfileData qProfileData = QProfileData.profileData;
        QActivityData qActivityData = QActivityData.activityData;

        MemberData memberData = queryFactory
                .select(qMemberData)
                .from(qMemberData)
                .join(qMemberData.profileData, qProfileData)
                .join(qMemberData.activityDataMap, qActivityData)
                .where(qMemberData.userId.eq(userId)
                        .and(qActivityData.userId.eq(userId))
                        .and(qProfileData.userId.eq(userId)))
                .fetchOne();

        return Optional.ofNullable(memberData);
    }
}
