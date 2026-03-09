package com.pfplaybackend.api.party.adapter.in.web.payload.request.dj;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RegisterDjRequest {
    @NotNull(message = "playlistId is required.")
    private Long playlistId;
}
