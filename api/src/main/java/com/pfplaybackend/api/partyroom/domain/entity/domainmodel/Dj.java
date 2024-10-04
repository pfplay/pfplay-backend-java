package com.pfplaybackend.api.partyroom.domain.entity.domainmodel;

import com.pfplaybackend.api.partyroom.domain.value.CrewId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaylistId;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Dj {
    private long id;
    private PartyroomId partyroomId;
    private UserId userId;
    private CrewId crewId;
    private PlaylistId playlistId;
    private int orderNumber;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Dj() {}

    public Dj(PartyroomId partyroomId, UserId userId, CrewId crewId, PlaylistId playlistId, int orderNumber, boolean isDeleted) {
        this.partyroomId = partyroomId;
        this.userId = userId;
        this.crewId = crewId;
        this.playlistId = playlistId;
        this.orderNumber = orderNumber;
        this.isDeleted = isDeleted;
    }

    @Builder
    public Dj(long id, PartyroomId partyroomId, UserId userId, CrewId crewId, PlaylistId playlistId, int orderNumber, boolean isDeleted,
              LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.partyroomId = partyroomId;
        this.userId = userId;
        this.crewId = crewId;
        this.playlistId = playlistId;
        this.orderNumber = orderNumber;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Dj assignPartyroomId(PartyroomId partyroomId) {
        this.partyroomId = partyroomId;
        return this;
    }

    public static Dj create(PartyroomId partyroomId, PlaylistId playlistId, UserId userId, CrewId crewId, int orderNumber) {
        return new Dj(partyroomId, userId, crewId, playlistId, orderNumber, false);
    }

    public void updateOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void applyDeleted() {
        this.isDeleted = true;
    }
}
