package com.pfplaybackend.api.party.application.dto.partyroom;

import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;

public record ActivePartyroomDto(Long id, boolean isQueueClosed, Long crewId,
                                 boolean isPlaybackActivated, PlaybackId currentPlaybackId,
                                 CrewId currentDjCrewId) {}
