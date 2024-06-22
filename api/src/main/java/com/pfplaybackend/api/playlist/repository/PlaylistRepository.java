package com.pfplaybackend.api.playlist.repository;

import com.pfplaybackend.api.playlist.domain.Playlist;
import com.pfplaybackend.api.playlist.domain.model.entity.PlaylistData;
import com.pfplaybackend.api.playlist.domain.model.enums.PlaylistType;
import com.pfplaybackend.api.user.domain.model.value.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<PlaylistData, Long> {
    Playlist findByIdAndOwnerIdAndType(Long id, UserId ownerId, PlaylistType type);

    // 내 플레이리스트, 그랩리스트 전체 조회
    List<Playlist> findByOwnerIdOrderByTypeDescOrderNumberAsc(UserId ownerId);

    // 내 플레이리스트 정렬하여 조회
    List<Playlist> findByOwnerIdAndTypeOrderByOrderNumberDesc(UserId ownerId, PlaylistType type);
}
