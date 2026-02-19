package com.pfplaybackend.api.playlist.adapter.in.web.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTrackOrderRequest {
    private Integer nextOrderNumber;
}
