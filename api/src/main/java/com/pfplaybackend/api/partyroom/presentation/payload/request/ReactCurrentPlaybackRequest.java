package com.pfplaybackend.api.partyroom.presentation.payload.request;

import com.pfplaybackend.api.partyroom.domain.enums.ReactionType;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class ReactCurrentPlaybackRequest {
    private ReactionType reactionType;
}