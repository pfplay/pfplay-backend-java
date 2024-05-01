package com.pfplaybackend.api.playlist.repository;

import com.pfplaybackend.api.playlist.model.entity.Playlist;
import com.pfplaybackend.api.playlist.model.enums.PlaylistType;
import com.pfplaybackend.api.user.model.value.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PlayListRepository extends JpaRepository<Playlist, Long> {
    Playlist findByIdAndUserIdAndType(Long id, UUID userId, PlaylistType type);

    // 내 플레이리스트, 그랩리스트 전체 조회
    List<Playlist> findByUserIdOrderByTypeDescOrderNumberAsc(UUID userId);

    // 내 플레이리스트 정렬하여 조회
    List<Playlist> findByUserIdAndTypeOrderByOrderNumberDesc(UserId uId, PlaylistType type);
}
