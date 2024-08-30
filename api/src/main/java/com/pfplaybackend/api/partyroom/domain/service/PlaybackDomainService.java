package com.pfplaybackend.api.partyroom.domain.service;

import org.springframework.stereotype.Service;

@Service
public class PlaybackDomainService {

    public long convertToSeconds(String duration) {
        String[] parts = duration.split(":");
        long minutes = Long.parseLong(parts[0]);
        long seconds = Long.parseLong(parts[1]);
        return minutes * 60 + seconds;
    }
}
