package com.pfplaybackend.api.playlist.adapter.out.persistence;

import com.pfplaybackend.api.playlist.domain.entity.data.TrackData;
import com.pfplaybackend.api.playlist.adapter.out.persistence.custom.TrackRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TrackRepository extends JpaRepository<TrackData, Long>, TrackRepositoryCustom {

    Optional<TrackData> findByPlaylistIdAndLinkId(Long playlistDataId, String linkId);
    TrackData findFirstByLinkId(String linkId);
    Optional<TrackData> findByIdAndPlaylistId(Long id, Long playlistDataId);
    boolean existsByPlaylistId(Long playlistDataId);

    @Modifying
    @Query("UPDATE TrackData pm SET pm.orderNumber = CASE " +
            "WHEN pm.orderNumber = 1 THEN :totalElements " +
            "ELSE pm.orderNumber - 1 END " +
            "WHERE pm.playlistId = :playlistId")
    void reorderTracks(@Param("playlistId") Long playlistId, @Param("totalElements") long totalElements);

    @Modifying
    @Query("UPDATE TrackData pm " +
            "SET pm.orderNumber =  pm.orderNumber - 1" +
            "WHERE pm.playlistId = :playlistId " +
            "AND pm.orderNumber > :deleteOrderNumber "
    )
    void shiftUpOrderByDelete(@Param("playlistId") Long playlistId, @Param("deleteOrderNumber") Integer deleteOrderNumber);

    @Modifying
    @Query("UPDATE TrackData pm " +
            "SET pm.orderNumber =  pm.orderNumber - 1" +
            "WHERE pm.playlistId = :playlistId " +
            "AND pm.orderNumber > :prevOrderNumber " +
            "AND pm.orderNumber <= :nextOrderNumber "
    )
    void shiftUpOrderByDnD(@Param("playlistId") Long playlistId, @Param("prevOrderNumber") Integer prevOrderNumber, @Param("nextOrderNumber") Integer nextOrderNumber);

    @Modifying
    @Query("UPDATE TrackData pm " +
            "SET pm.orderNumber =  pm.orderNumber + 1" +
            "WHERE pm.playlistId = :playlistId " +
            "AND pm.orderNumber < :prevOrderNumber " +
            "AND pm.orderNumber >= :nextOrderNumber "
    )
    void shiftDownOrderByDnD(@Param("playlistId") Long playlistId, @Param("prevOrderNumber") Integer prevOrderNumber, @Param("nextOrderNumber") Integer nextOrderNumber);
}