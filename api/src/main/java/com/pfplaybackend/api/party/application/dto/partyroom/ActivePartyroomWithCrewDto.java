package com.pfplaybackend.api.party.application.dto.partyroom;

import com.pfplaybackend.api.party.domain.value.PlaybackId;

public record ActivePartyroomWithCrewDto(Long id, boolean isPlaybackActivated, boolean isQueueClosed, PlaybackId currentPlaybackId, Long crewId) {}
