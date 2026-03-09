package com.pfplaybackend.api.playlist.adapter.in.web.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTrackOrderRequest {
    @NotNull(message = "nextOrderNumber is required.")
    @Min(value = 0, message = "nextOrderNumber must be 0 or greater.")
    private Integer nextOrderNumber;
}
