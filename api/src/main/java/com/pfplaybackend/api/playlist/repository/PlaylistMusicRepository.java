package com.pfplaybackend.api.playlist.repository;

import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistMusicData;
import com.pfplaybackend.api.playlist.repository.custom.PlaylistMusicRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlaylistMusicRepository extends JpaRepository<PlaylistMusicData, Long>, PlaylistMusicRepositoryCustom {
    Page<PlaylistMusicData> findByPlaylistDataIdOrderByOrderNumber(Pageable pageable, Long playlistDataId);
    List<PlaylistMusicData> findAllByPlaylistDataId(Long playlistDataId);
    double countByPlaylistDataId(Long playlistDataId);

    Optional<PlaylistMusicData> findByPlaylistDataIdAndLinkId(Long playlistDataId, String linkId);
    PlaylistMusicData findFirstByLinkId(String linkId);

    @Modifying
    @Query("UPDATE PlaylistMusicData pm SET pm.orderNumber = CASE " +
            "WHEN pm.orderNumber = 1 THEN :totalElements " +
            "ELSE pm.orderNumber - 1 END " +
            "WHERE pm.playlistData.id = :playlistId")
    void reorderMusics(@Param("playlistId") Long playlistId, @Param("totalElements") long totalElements);
}