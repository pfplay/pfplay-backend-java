package com.pfplaybackend.api.user.repository.impl;

import com.pfplaybackend.api.user.model.entity.Member;
import com.pfplaybackend.api.user.model.entity.QMember;
import com.pfplaybackend.api.user.model.entity.QProfile;
import com.pfplaybackend.api.user.model.value.UserId;
import com.pfplaybackend.api.user.repository.custom.MemberRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

public class MemberRepositoryImpl implements MemberRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Member> findCustomQueryMethod(UserId userId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMember qMember = QMember.member;
        QProfile qProfile = QProfile.profile;

        Member member = queryFactory
                .select(qMember)
                .from(qMember)
                .join(qMember.profile, qProfile)
                .where(qProfile.userId.eq(qMember.userId))
                .fetchOne();

        return Optional.ofNullable(member);
    }
}
