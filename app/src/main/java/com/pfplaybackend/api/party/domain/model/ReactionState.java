package com.pfplaybackend.api.party.domain.model;

import com.pfplaybackend.api.party.domain.enums.ReactionType;

public record ReactionState(boolean liked, boolean disliked, boolean grabbed) {

    public static ReactionState createBaseState() {
        return new ReactionState(false, false, false);
    }

    public static ReactionState createState(ReactionType reactionType) {
        return new ReactionState(reactionType.isLiked(), reactionType.isDisliked(), reactionType.isGrabbed());
    }
}
