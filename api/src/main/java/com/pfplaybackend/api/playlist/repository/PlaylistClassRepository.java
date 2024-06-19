package com.pfplaybackend.api.playlist.repository;

import com.pfplaybackend.api.playlist.domain.model.enums.PlaylistType;
import com.pfplaybackend.api.user.domain.model.value.UserId;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.pfplaybackend.api.playlist.domain.model.entity.QPlaylistData.playlistData;
import static com.pfplaybackend.api.playlist.domain.model.entity.QPlaylistMusicData.playlistMusicData;

@RequiredArgsConstructor
@Repository
public class PlaylistClassRepository {
    private final JPAQueryFactory queryFactory;

    public List<Tuple> findByUserId(UserId userId) {

        return queryFactory
                .select(
                        playlistData.id,
                        playlistData.name,
                        playlistData.orderNumber,
                        playlistData.type,
                        playlistMusicData.id.count().as("count")
                )
                .from(playlistData)
                .leftJoin(playlistMusicData).on(playlistData.id.eq(playlistMusicData.playlistData.id))
                .where(playlistData.userId.eq(userId))
                .groupBy(playlistData.id)
                .orderBy(playlistData.type.desc(), playlistData.orderNumber.asc())
                .fetch();
    }

    public List<Long> findByUserIdAndListIdAndType(UserId userId, List<Long> listIds, PlaylistType type){
        return queryFactory
                .select(
                        playlistData.id
                )
                .from(playlistData)
                .where(playlistData.userId.eq(userId)
                        .and(playlistData.id.in(listIds))
                        .and(playlistData.type.eq(type)))
                .groupBy(playlistData.id)
                .orderBy(playlistData.type.desc(), playlistData.orderNumber.asc())
                .fetch();
    }

    public Long deleteByListIds(List<Long> listIds){
        return queryFactory
                .delete(playlistData)
                .where(playlistData.id.in(listIds))
                .execute();
    }
}