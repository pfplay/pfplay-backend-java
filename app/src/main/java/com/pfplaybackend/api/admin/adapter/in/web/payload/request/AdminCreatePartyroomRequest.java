package com.pfplaybackend.api.admin.adapter.in.web.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request DTO for admin to create a partyroom with designated host
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreatePartyroomRequest {

    /**
     * User ID (UUID) of the member who will be the HOST
     * This user will be automatically entered into the partyroom with HOST grade
     */
    @NotBlank(message = "Host user ID is required")
    private String hostUserId;

    /**
     * Partyroom title
     */
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be less than 100 characters")
    private String title;

    /**
     * Partyroom introduction/description
     */
    @Size(max = 500, message = "Introduction must be less than 500 characters")
    private String introduction;

    /**
     * Custom link domain for the partyroom
     * If empty, will be auto-generated
     */
    private String linkDomain;

    /**
     * Playback time limit per track (in seconds)
     */
    @NotNull(message = "Playback time limit is required")
    @Min(value = 1, message = "Playback time limit must be at least 1 second")
    private Integer playbackTimeLimit;
}
