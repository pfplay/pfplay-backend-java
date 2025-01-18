package com.pfplaybackend.api.playlist.repository;

import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistMusicData;
import com.pfplaybackend.api.playlist.repository.custom.TrackRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrackRepository extends JpaRepository<PlaylistMusicData, Long>, TrackRepositoryCustom {

    Optional<PlaylistMusicData> findByPlaylistDataIdAndLinkId(Long playlistDataId, String linkId);
    PlaylistMusicData findFirstByLinkId(String linkId);
    Optional<PlaylistMusicData> findByIdAndPlaylistDataId(Long id, Long playlistDataId);

    @Modifying
    @Query("UPDATE PlaylistMusicData pm SET pm.orderNumber = CASE " +
            "WHEN pm.orderNumber = 1 THEN :totalElements " +
            "ELSE pm.orderNumber - 1 END " +
            "WHERE pm.playlistData.id = :playlistId")
    void reorderMusics(@Param("playlistId") Long playlistId, @Param("totalElements") long totalElements);

    @Modifying
    @Query("UPDATE PlaylistMusicData pm " +
            "SET pm.orderNumber =  pm.orderNumber - 1" +
            "WHERE pm.playlistData.id = :playlistId " +
            "AND pm.orderNumber > :deleteOrderNumber "
    )
    void shiftUpOrderByDelete(@Param("playlistId") Long playlistId, Integer deleteOrderNumber);

    @Modifying
    @Query("UPDATE PlaylistMusicData pm " +
            "SET pm.orderNumber =  pm.orderNumber - 1" +
            "WHERE pm.playlistData.id = :playlistId " +
            "AND pm.orderNumber > :prevOrderNumber " +
            "AND pm.orderNumber <= :nextOrderNumber "
    )
    void shiftUpOrderByDnD(@Param("playlistId") Long playlistId, @Param("prevOrderNumber") Integer prevOrderNumber, @Param("nextOrderNumber") Integer nextOrderNumber);

    @Modifying
    @Query("UPDATE PlaylistMusicData pm " +
            "SET pm.orderNumber =  pm.orderNumber + 1" +
            "WHERE pm.playlistData.id = :playlistId " +
            "AND pm.orderNumber < :prevOrderNumber " +
            "AND pm.orderNumber >= :nextOrderNumber "
    )
    void shiftDownOrderByDnD(@Param("playlistId") Long playlistId, @Param("prevOrderNumber") Integer prevOrderNumber, @Param("nextOrderNumber") Integer nextOrderNumber);
}