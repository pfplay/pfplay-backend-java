package com.pfplaybackend.api.party.adapter.out.persistence;

import com.pfplaybackend.api.party.domain.entity.data.PartyroomPlaybackData;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyroomPlaybackRepository extends JpaRepository<PartyroomPlaybackData, PartyroomId> {
}
