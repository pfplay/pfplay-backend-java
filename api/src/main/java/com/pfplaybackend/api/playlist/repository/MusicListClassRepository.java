package com.pfplaybackend.api.playlist.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.pfplaybackend.api.entity.QMusicList.musicList;

@RequiredArgsConstructor
@Repository
public class MusicListClassRepository {
    private final JPAQueryFactory queryFactory;

    public Long deleteByPlayListIds(List<Long> listIds) {
        return queryFactory
                .delete(musicList)
                .where(musicList.playList.id.in(listIds))
                .execute();
    }

    public Long deleteByIdsAndPlayListId(List<Long> ids, Long playListId) {
        return queryFactory
                .delete(musicList)
                .where(musicList.id.in(ids)
                        .and(musicList.playList.id.eq(playListId)))
                .execute();
    }
}
