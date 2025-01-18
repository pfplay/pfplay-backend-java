package com.pfplaybackend.api.playlist.presentation.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTrackOrderRequest {
    private Integer nextOrderNumber;
}
