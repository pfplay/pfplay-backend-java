package com.pfplaybackend.api.playlist.repository;

import com.pfplaybackend.api.entity.PlayList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayListRepository extends JpaRepository<PlayList, Long> {
    List<PlayList> findByUserId(Long userId);

    Optional<PlayList> findTopByUserIdOrderByOrderNumberDesc(Long userId);
}
