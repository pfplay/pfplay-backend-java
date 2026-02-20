package com.pfplaybackend.api.party.domain.enums;

import lombok.Getter;

@Getter
public enum ReactionType {
    LIKE(true, false, false),
    DISLIKE(false, true, false),
    GRAB(true, false, true);

    private final boolean liked;
    private final boolean disliked;
    private final boolean grabbed;

    ReactionType(boolean liked, boolean disliked, boolean grabbed) {
        this.liked = liked;
        this.disliked = disliked;
        this.grabbed = grabbed;
    }
}
