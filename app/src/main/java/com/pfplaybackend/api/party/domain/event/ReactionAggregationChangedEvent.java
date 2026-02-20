package com.pfplaybackend.api.party.domain.event;

import com.pfplaybackend.api.common.domain.event.DomainEvent;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import lombok.Getter;

@Getter
public class ReactionAggregationChangedEvent extends DomainEvent {
    private final PartyroomId partyroomId;
    private final int likeCount;
    private final int dislikeCount;
    private final int grabCount;

    public ReactionAggregationChangedEvent(PartyroomId partyroomId, int likeCount,
                                            int dislikeCount, int grabCount) {
        this.partyroomId = partyroomId;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.grabCount = grabCount;
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(partyroomId.getId());
    }
}
