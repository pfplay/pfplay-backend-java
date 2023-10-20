package com.pfplaybackend.api.playlist.repository;

import com.pfplaybackend.api.entity.PlayList;
import com.pfplaybackend.api.playlist.enums.PlayListType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayListRepository extends JpaRepository<PlayList, Long> {
    List<PlayList> findByUserIdAndTypeOrderByOrderNumberDesc(Long userId, PlayListType type);
    List<PlayList> findByUserIdAndTypeOrderByOrderNumberAsc(Long userId, PlayListType type);

    Optional<PlayList> findTopByUserIdOrderByOrderNumberDesc(Long userId);
}
