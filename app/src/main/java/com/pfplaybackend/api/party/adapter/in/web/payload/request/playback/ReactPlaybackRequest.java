package com.pfplaybackend.api.party.adapter.in.web.payload.request.playback;

import com.pfplaybackend.api.party.domain.enums.ReactionType;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class ReactPlaybackRequest {
    private ReactionType reactionType;
}
