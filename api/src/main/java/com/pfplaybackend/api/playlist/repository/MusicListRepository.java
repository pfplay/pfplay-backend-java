package com.pfplaybackend.api.playlist.repository;

import com.pfplaybackend.api.entity.MusicList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MusicListRepository extends JpaRepository<MusicList, Long> {
    Page<MusicList> findByPlayListIdOrderByOrderNumber(Pageable pageable, Long playListId);

    List<MusicList> findAllByPlayListId(Long playListId);

    double countByPlayListId(Long playListId);
}
