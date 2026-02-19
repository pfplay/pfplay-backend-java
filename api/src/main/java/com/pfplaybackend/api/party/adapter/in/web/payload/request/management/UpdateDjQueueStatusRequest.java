package com.pfplaybackend.api.party.adapter.in.web.payload.request.management;

import com.pfplaybackend.api.party.domain.enums.QueueStatus;
import lombok.Data;

@Data
public class UpdateDjQueueStatusRequest {
    private QueueStatus queueStatus;
}
