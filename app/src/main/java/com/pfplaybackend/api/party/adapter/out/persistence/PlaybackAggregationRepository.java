package com.pfplaybackend.api.party.adapter.out.persistence;

import com.pfplaybackend.api.party.domain.entity.data.PlaybackAggregationData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaybackAggregationRepository extends JpaRepository<PlaybackAggregationData, Long> {
}
