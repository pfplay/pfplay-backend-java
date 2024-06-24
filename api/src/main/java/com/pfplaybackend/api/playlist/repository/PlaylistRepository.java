package com.pfplaybackend.api.playlist.repository;

import com.pfplaybackend.api.playlist.domain.entity.domainmodel.Playlist;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import com.pfplaybackend.api.playlist.repository.custom.PlaylistRepositoryCustom;
import com.pfplaybackend.api.user.domain.value.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<PlaylistData, Long>, PlaylistRepositoryCustom {
    List<PlaylistData> findAllByOwnerId(UserId userId);
    List<PlaylistData> findByOwnerIdAndTypeOrderByOrderNumberDesc(UserId userId, PlaylistType type);
}