package com.pfplaybackend.api.playlist.repository;

import com.pfplaybackend.api.playlist.model.entity.PlaylistMusic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MusicListRepository extends JpaRepository<PlaylistMusic, Long> {
    Page<PlaylistMusic> findByPlaylistIdOrderByOrderNumber(Pageable pageable, Long playlistId);
    List<PlaylistMusic> findAllByPlaylistId(Long playlistId);
    double countByPlaylistId(Long playlistId);
}
