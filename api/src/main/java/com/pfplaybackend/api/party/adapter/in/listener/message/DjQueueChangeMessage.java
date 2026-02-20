package com.pfplaybackend.api.party.adapter.in.listener.message;

import com.pfplaybackend.api.party.application.dto.dj.DjWithProfileDto;
import com.pfplaybackend.api.common.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DjQueueChangeMessage implements Serializable {
    private PartyroomId partyroomId;
    private MessageTopic eventType;
    private List<DjWithProfileDto> djs;

    public static DjQueueChangeMessage create(PartyroomId partyroomId, List<DjWithProfileDto> djs) {
        return new DjQueueChangeMessage(
                partyroomId,
                MessageTopic.DJ_QUEUE_CHANGE,
                djs);
    }
}
