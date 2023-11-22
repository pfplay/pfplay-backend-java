package com.pfplaybackend.api.partyroom.repository.dsl;

import com.pfplaybackend.api.entity.QPartyRoomJoin;
import com.pfplaybackend.api.partyroom.presentation.dto.PartyRoomHomeResultDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.pfplaybackend.api.entity.QPartyPermission.partyPermission;
import static com.pfplaybackend.api.entity.QPartyRoom.partyRoom;
import static com.pfplaybackend.api.entity.QPartyRoomJoin.partyRoomJoin;
import static com.pfplaybackend.api.entity.QUser.user;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PartyRoomRepositorySupport {

    private final JPAQueryFactory query;
    public PageImpl<PartyRoomHomeResultDto> findAll(Pageable pageable) {
        NumberPath<Long> aliasUserCount = Expressions.numberPath(Long.class, "userCount");
        JPAQuery<Tuple> tupleJPAQuery = query.select(
                        partyRoomJoin.user.id.count().as(aliasUserCount),
                        partyRoom.introduce,
                        partyRoom.createdAt,
                        ExpressionUtils.as(WithUserSubQuery(user.nickname), "nickname"),
                        ExpressionUtils.as(WithUserSubQuery(user.faceUrl), "face_url"),
                        ExpressionUtils.as(WithUserSubQuery(partyPermission.authority), "role")
                ).from(partyRoom)
                .leftJoin(partyRoomJoin).on(partyRoomJoin.partyRoom.id.eq(partyRoom.id))
                .leftJoin(user).on(user.id.eq(partyRoomJoin.user.id))
                .groupBy(partyRoom.id, partyRoom.introduce, partyRoom.createdAt)
                .orderBy(aliasUserCount.desc(), partyRoom.createdAt.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<PartyRoomHomeResultDto> result = tupleJPAQuery.stream()
                .map(tuple -> {
                    String nickname = tuple.get(Expressions.stringPath("nickname"));
                    String faceUrl = tuple.get(Expressions.stringPath("face_url"));

                    final int showProfileCount = (nickname != null) ? nickname.split(",").length : 0;
                    List<PartyRoomHomeResultDto.Participant> partyRoomHomeResultAttendInfo =
                            IntStream.range(0, showProfileCount)
                            .boxed()
                            .map(i -> {
                                String[] nicknames = (nickname != null) ? nickname.split(",") : new String[0];
                                String[] faceUrls = (faceUrl != null) ? faceUrl.split(",") : new String[0];
                                return PartyRoomHomeResultDto.Participant.builder()
                                            .nickname(nicknames[i])
                                            .faceUrl(faceUrls[i])
                                            .build();
                            }).collect(Collectors.toList());

                    return new PartyRoomHomeResultDto(
                            tuple.get(partyRoom.introduce),
                            tuple.get(partyRoom.createdAt),
                            tuple.get(aliasUserCount),
                            partyRoomHomeResultAttendInfo
                    );
                })
                .collect(Collectors.toList());

        long total = result.size();
        return new PageImpl<>(result, pageable, total);
    }

    private JPQLQuery<?> WithUserSubQuery(Path<?> field) {
        QPartyRoomJoin QPartyRoomJoin = new QPartyRoomJoin("sub");
        return JPAExpressions
                .select(Expressions.stringTemplate("GROUP_CONCAT({0})", field))
                .from(QPartyRoomJoin)
                .innerJoin(user).on(QPartyRoomJoin.user.id.eq(user.id))
                .innerJoin(partyPermission).on(QPartyRoomJoin.role.eq(partyPermission.authority))
                .where(QPartyRoomJoin.partyRoom.id.eq(partyRoom.id))
                .orderBy(partyPermission.level.asc())
                .limit(3);
    }

}
