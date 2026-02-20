package com.pfplaybackend.api.party.domain.value;

import java.io.Serializable;

public record PlaybackSnapshot(
        long id,
        String linkId,
        String name,
        String duration,
        String thumbnailImage,
        long endTime
) implements Serializable {}
