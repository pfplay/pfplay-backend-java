package com.pfplaybackend.api.user.repository.impl;

import com.pfplaybackend.api.user.domain.entity.data.GuestData;
import com.pfplaybackend.api.user.domain.entity.data.QGuestData;
import com.pfplaybackend.api.user.domain.entity.data.QProfileData;
import com.pfplaybackend.api.user.domain.value.UserId;
import com.pfplaybackend.api.user.repository.custom.GuestRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;

public class GuestRepositoryImpl implements GuestRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<GuestData> findByUserId(UserId userId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QGuestData qGuestData = QGuestData.guestData;
        QProfileData qProfileData = QProfileData.profileData;

        GuestData guestData = queryFactory
                .select(qGuestData)
                .from(qGuestData)
                .join(qGuestData.profileData, qProfileData)
                .where(qProfileData.userId.eq(qGuestData.userId)
                        .and(qGuestData.userId.eq(userId)))
                .fetchOne();

        return Optional.ofNullable(guestData);
    }
}
