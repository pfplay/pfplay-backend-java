package com.pfplaybackend.api.partyroom.domain.entity.data.history;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.partyroom.domain.model.ReactionState;
import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@DynamicInsert
@DynamicUpdate
@Table(
        name = "PLAYBACK_REACTION_HISTORY"
)
@Entity
public class PlaybackReactionHistoryData extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "uid", column = @Column(name = "user_id")),
    })
    private UserId userId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "playback_id")),
    })
    private PlaybackId playbackId;

    @Column(name = "liked")
    private boolean liked;

    @Column(name = "disliked")
    private boolean disliked;

    @Column(name = "grabbed")
    private boolean grabbed;

    public PlaybackReactionHistoryData() {}

    public PlaybackReactionHistoryData(UserId userId, PlaybackId playbackId) {
        this.userId = userId;
        this.playbackId = playbackId;
        this.liked = false;
        this.disliked = false;
        this.grabbed = false;
    }

    public PlaybackReactionHistoryData applyReactionState(ReactionState reactionState) {
        this.liked = reactionState.isLiked();
        this.disliked = reactionState.isDisliked();
        this.grabbed = reactionState.isGrabbed();
        return this;
    }
}
