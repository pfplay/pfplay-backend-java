package com.pfplaybackend.api.partyroom.presentation.payload.request;

import com.pfplaybackend.api.partyroom.domain.enums.QueueStatus;
import lombok.Data;

@Data
public class UpdateDjQueueStatusRequest {
    private QueueStatus queueStatus;
}
