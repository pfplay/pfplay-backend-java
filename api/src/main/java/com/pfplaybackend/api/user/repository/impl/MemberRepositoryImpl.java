package com.pfplaybackend.api.user.repository.impl;

import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.entity.data.QActivityData;
import com.pfplaybackend.api.user.domain.entity.data.QMemberData;
import com.pfplaybackend.api.user.domain.entity.data.QProfileData;
import com.pfplaybackend.api.user.domain.value.UserId;
import com.pfplaybackend.api.user.repository.custom.MemberRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<MemberData> findByUserId(UserId userId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
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
