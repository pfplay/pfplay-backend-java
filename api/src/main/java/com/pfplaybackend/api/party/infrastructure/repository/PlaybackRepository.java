package com.pfplaybackend.api.party.infrastructure.repository;

import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaybackRepository extends JpaRepository<PlaybackData, Long> {
    Optional<PlaybackData> findById(Long id);
}
