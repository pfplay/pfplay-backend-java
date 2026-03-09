package com.pfplaybackend.api.party.adapter.in.web.payload.request.management;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePartyroomRequest {
    @NotBlank(message = "title is required.")
    @Size(max = 100, message = "title must be 100 characters or less.")
    private String title;
    @Size(max = 500, message = "introduction must be 500 characters or less.")
    private String introduction;
    private String linkDomain;
    @NotNull(message = "playbackTimeLimit is required.")
    @Min(value = 1, message = "playbackTimeLimit must be at least 1.")
    private Integer playbackTimeLimit;
}
