package com.pfplaybackend.api.playlist.repository;


import com.pfplaybackend.api.entity.PlayList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayListRepository extends JpaRepository<PlayList, Long> {

    PlayList findByUserId(Long userId);

}
