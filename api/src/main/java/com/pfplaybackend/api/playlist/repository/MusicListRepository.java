package com.pfplaybackend.api.playlist.repository;

import com.pfplaybackend.api.entity.MusicList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MusicListRepository extends JpaRepository<MusicList, Long> {
    List<MusicList> findByPlayListIdOrderByOrderNumber(Long playListId);


}
