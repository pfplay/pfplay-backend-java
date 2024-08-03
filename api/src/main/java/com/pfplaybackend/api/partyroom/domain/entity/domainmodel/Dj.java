package com.pfplaybackend.api.partyroom.domain.entity.domainmodel;

import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaylistId;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Dj {
    private long id;
    private PartyroomId partyroomId;
    private UserId userId;
    private PlaylistId playlistId;
    private int orderNumber;
    private boolean isDeleted;

    public Dj() {}

    public Dj(PartyroomId partyroomId, UserId userId, PlaylistId playlistId, int orderNumber, boolean isDeleted) {
        this.partyroomId = partyroomId;
        this.userId = userId;
        this.playlistId = playlistId;
        this.orderNumber = orderNumber;
        this.isDeleted = isDeleted;
    }

    @Builder
    public Dj(long id, PartyroomId partyroomId, UserId userId, PlaylistId playlistId, int orderNumber, boolean isDeleted) {
        this.id = id;
        this.partyroomId = partyroomId;
        this.userId = userId;
        this.playlistId = playlistId;
        this.orderNumber = orderNumber;
        this.isDeleted = isDeleted;
    }

    public Dj assignPartyroomId(PartyroomId partyroomId) {
        this.partyroomId = partyroomId;
        return this;
    }

    public static Dj create(PartyroomId partyroomId, PlaylistId playlistId, UserId userId, int orderNumber) {
        return new Dj(partyroomId, userId, playlistId, orderNumber, false);
    }

    public Dj updateOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
        return this;
    }
}
