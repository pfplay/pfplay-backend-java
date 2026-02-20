package com.pfplaybackend.api.party.domain.entity.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.party.domain.event.PlaybackDeactivatedEvent;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@DynamicInsert
@DynamicUpdate
@Table(name = "PARTYROOM_PLAYBACK")
@Entity
public class PartyroomPlaybackData extends BaseEntity {

    @Id
    @Column(name = "partyroom_id")
    private Long partyroomId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "current_playback_id")),
    })
    private PlaybackId currentPlaybackId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "current_dj_crew_id")),
    })
    private CrewId currentDjCrewId;

    private boolean isActivated;

    protected PartyroomPlaybackData() {}

    private PartyroomPlaybackData(Long partyroomId) {
        this.partyroomId = partyroomId;
        this.isActivated = false;
    }

    // ── Factory Method ──

    public static PartyroomPlaybackData createFor(Long partyroomId) {
        return new PartyroomPlaybackData(partyroomId);
    }

    // ── Business Methods ──

    public void activate(PlaybackId playbackId, CrewId djCrewId) {
        this.currentPlaybackId = playbackId;
        this.currentDjCrewId = djCrewId;
        this.isActivated = true;
    }

    public void deactivate() {
        this.currentPlaybackId = null;
        this.currentDjCrewId = null;
        this.isActivated = false;
        registerEvent(new PlaybackDeactivatedEvent(new PartyroomId(this.partyroomId)));
    }

    public void updatePlayback(PlaybackId playbackId, CrewId djCrewId) {
        this.currentPlaybackId = playbackId;
        this.currentDjCrewId = djCrewId;
    }

    public boolean isCurrentDj(CrewId crewId) {
        return currentDjCrewId != null && currentDjCrewId.equals(crewId);
    }
}
