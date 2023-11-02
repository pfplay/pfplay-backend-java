package com.pfplaybackend.api.playlist.repository;

import com.pfplaybackend.api.entity.PlayList;
import com.pfplaybackend.api.playlist.enums.PlayListType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayListRepository extends JpaRepository<PlayList, Long> {
    // 내 플레이리스트, 그랩리스트 전체 조회
    List<PlayList> findByUserIdOrderByTypeDescOrderNumberAsc(Long userId);

    // 내 플레이리스트 정렬하여 조회
    List<PlayList> findByUserIdAndTypeOrderByOrderNumberDesc(Long userId, PlayListType type);
}
