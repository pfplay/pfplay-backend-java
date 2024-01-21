package com.pfplaybackend.api.playlist.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.pfplaybackend.api.entity.QMusicList.musicList;
import static com.pfplaybackend.api.entity.QPlayList.playList;

@RequiredArgsConstructor
@Repository
public class PlayListClassRepository {
    private final JPAQueryFactory queryFactory;

    public List<Tuple> findByUserId(Long userId) {

        return queryFactory
                .select(
                        playList.id,
                        playList.name,
                        playList.orderNumber,
                        playList.type,
                        musicList.id.count().as("count")
                )
                .from(playList)
                .leftJoin(musicList).on(playList.id.eq(musicList.playList.id))
                .where(playList.user.id.eq(userId))
                .groupBy(playList.id)
                .orderBy(playList.type.desc(), playList.orderNumber.asc())
                .fetch();
    }
}
