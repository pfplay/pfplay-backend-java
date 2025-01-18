package com.pfplaybackend.api.party.interfaces.api.rest.payload.request.playback;

import com.pfplaybackend.api.party.domain.enums.ReactionType;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class ReactCurrentPlaybackRequest {
    private ReactionType reactionType;
}