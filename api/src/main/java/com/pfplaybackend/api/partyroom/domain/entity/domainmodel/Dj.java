package com.pfplaybackend.api.partyroom.domain.entity.domainmodel;

import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaylistId;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Dj {
    private long id;
    private PartyroomId partyroomId;
    private PlaylistId playlistId;
    private int orderNumber;
    private boolean isDeleted;

    public Dj() {}

    @Builder
    public Dj(long id, PartyroomId partyroomId, PlaylistId playlistId, int orderNumber, boolean isDeleted) {
        this.id = id;
        this.partyroomId = partyroomId;
        this.playlistId = playlistId;
        this.orderNumber = orderNumber;
        this.isDeleted = isDeleted;
    }

    public Dj assignPartyroomId(PartyroomId partyroomId) {
        this.partyroomId = partyroomId;
        return this;
    }
}
