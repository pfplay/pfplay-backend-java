package com.pfplaybackend.api.party.adapter.in.web.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddBlockRequest {
    @NotNull(message = "crewId is required.")
    private Long crewId;
}
