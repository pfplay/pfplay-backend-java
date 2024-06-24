package com.pfplaybackend.api.playlist.repository;

import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistMusicData;
import com.pfplaybackend.api.playlist.repository.custom.PlaylistMusicRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistMusicRepository extends JpaRepository<PlaylistMusicData, Long>, PlaylistMusicRepositoryCustom {
    Page<PlaylistMusicData> findByPlaylistDataIdOrderByOrderNumber(Pageable pageable, Long playlistDataId);
    List<PlaylistMusicData> findAllByPlaylistDataId(Long playlistDataId);
    double countByPlaylistDataId(Long playlistDataId);
}