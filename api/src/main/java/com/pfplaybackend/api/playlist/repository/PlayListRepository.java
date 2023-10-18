package com.pfplaybackend.api.playlist.repository;

import com.pfplaybackend.api.entity.PlayList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayListRepository extends JpaRepository<PlayList, Long> {
    PlayList findByUserId(Long userId);

    Optional<PlayList> findTopByUserIdOrderByOrderNumberDesc(Long userId);
}
