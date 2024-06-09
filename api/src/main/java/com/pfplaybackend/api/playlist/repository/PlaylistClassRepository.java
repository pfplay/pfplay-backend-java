package com.pfplaybackend.api.playlist.repository;

import com.pfplaybackend.api.playlist.model.enums.PlaylistType;
import com.pfplaybackend.api.user.model.value.UserId;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.pfplaybackend.api.playlist.model.entity.QPlaylist.playlist;
import static com.pfplaybackend.api.playlist.model.entity.QPlaylistMusic.playlistMusic;

@RequiredArgsConstructor
@Repository
public class PlaylistClassRepository {
    private final JPAQueryFactory queryFactory;

    public List<Tuple> findByUserId(UserId userId) {

        return queryFactory
                .select(
                        playlist.id,
                        playlist.name,
                        playlist.orderNumber,
                        playlist.type,
                        playlistMusic.id.count().as("count")
                )
                .from(playlist)
                .leftJoin(playlistMusic).on(playlist.id.eq(playlistMusic.playlist.id))
                .where(playlist.userId.eq(userId))
                .groupBy(playlist.id)
                .orderBy(playlist.type.desc(), playlist.orderNumber.asc())
                .fetch();
    }

    public List<Long> findByUserIdAndListIdAndType(UserId userId, List<Long> listIds, PlaylistType type){
        return queryFactory
                .select(
                        playlist.id
                )
                .from(playlist)
                .where(playlist.userId.eq(userId)
                        .and(playlist.id.in(listIds))
                        .and(playlist.type.eq(type)))
                .groupBy(playlist.id)
                .orderBy(playlist.type.desc(), playlist.orderNumber.asc())
                .fetch();
    }

    public Long deleteByListIds(List<Long> listIds){
        return queryFactory
                .delete(playlist)
                .where(playlist.id.in(listIds))
                .execute();
    }
}