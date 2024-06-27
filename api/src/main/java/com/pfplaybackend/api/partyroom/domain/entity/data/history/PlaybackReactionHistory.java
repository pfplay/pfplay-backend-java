package com.pfplaybackend.api.partyroom.domain.entity.data.history;

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
public class PlaybackReactionHistory {
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

    @Column(name = "liked", nullable = false)
    private boolean liked;

    @Column(name = "disliked", nullable = false)
    private boolean disliked;

    public PlaybackReactionHistory() {}
}
