package com.pfplaybackend.api.playlist.adapter.out.persistence;

import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import com.pfplaybackend.api.playlist.adapter.out.persistence.custom.PlaylistRepositoryCustom;
import com.pfplaybackend.api.common.domain.value.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<PlaylistData, Long>, PlaylistRepositoryCustom {
    List<PlaylistData> findAllByOwnerId(UserId userId);
    List<PlaylistData> findByOwnerIdAndTypeOrderByOrderNumberDesc(UserId userId, PlaylistType type);
    PlaylistData findByOwnerIdAndType(UserId userId, PlaylistType type);
    Optional<PlaylistData> findByIdAndOwnerIdAndType(Long playlistId, UserId userId, PlaylistType type);
    Optional<PlaylistData> findByIdAndOwnerId(Long playlistId, UserId userId);
}