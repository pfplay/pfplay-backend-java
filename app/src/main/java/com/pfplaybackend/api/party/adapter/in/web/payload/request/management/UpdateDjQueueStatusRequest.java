package com.pfplaybackend.api.party.adapter.in.web.payload.request.management;

import com.pfplaybackend.api.party.domain.enums.QueueStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateDjQueueStatusRequest {
    @NotNull(message = "queueStatus is required.")
    private QueueStatus queueStatus;
}
