package com.pfplaybackend.api.partyroom.domain.entity.data.history;

import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
import com.pfplaybackend.api.partyroom.presentation.PlaybackReactionController;
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
public class PlaybackReactionHistoryData {
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

    // 그랩 호출 시 playlist_music 테이블의 식별자를 저장
    // 그랩 취소 시 해당 값을 이용하여, 레코드 삭제 가능
    @Column(name = "grabbed_music_id")
    private Long grabbedMusicId;

    public PlaybackReactionHistoryData() {}

    public PlaybackReactionHistoryData(UserId userId, PlaybackId playbackId) {
        this.userId = userId;
        this.playbackId = playbackId;
        this.liked = false;
        this.disliked = false;
        this.grabbed = false;
        this.grabbedMusicId = null;
    }

    public PlaybackReactionHistoryData applyLikedReaction() {
        this.liked = true;
        this.disliked = false;
        return this;
    }

    public PlaybackReactionHistoryData applyDislikedReaction() {
        this.liked = false;
        this.disliked = true;
        this.grabbed = false;
        this.grabbedMusicId = null;
        return this;
    }

    public PlaybackReactionHistoryData applyGrabbedReaction(long grabbedMusicId) {
        this.disliked = false;
        this.grabbed = true;
        this.grabbedMusicId = grabbedMusicId;
        return this;
    }
}
