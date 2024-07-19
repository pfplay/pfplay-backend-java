package com.pfplaybackend.api.partyroom.domain.model;

import com.pfplaybackend.api.partyroom.domain.enums.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class ReactionState {
    boolean isLiked;
    boolean isDisliked;
    boolean isGrabbed;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReactionState that = (ReactionState) o;
        return isLiked == that.isLiked &&
                isDisliked == that.isDisliked &&
                isGrabbed == that.isGrabbed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isLiked, isDisliked, isGrabbed);
    }

    public static ReactionState createBaseState() {
        return new ReactionState(false, false, false);
    }

    public static ReactionState createState(ReactionType reactionType) {
        if(reactionType.equals(ReactionType.LIKE)) {
            return new ReactionState(true, false, false);
        }
        if(reactionType.equals(ReactionType.DISLIKE)) {
            return new ReactionState(false, true, false);
        }
        if(reactionType.equals(ReactionType.GRAB)) {
            return new ReactionState(true, false, true);
        }
        // TODO Throw Exception;
        return null;
    }
}
