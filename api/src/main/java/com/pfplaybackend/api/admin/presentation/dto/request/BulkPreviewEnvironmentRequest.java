package com.pfplaybackend.api.admin.presentation.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating bulk preview/test environment
 * Creates multiple partyrooms with virtual members
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BulkPreviewEnvironmentRequest {

    /**
     * Number of partyrooms to create
     */
    @NotNull(message = "Partyroom count is required")
    @Min(value = 1, message = "Must create at least 1 partyroom")
    @Max(value = 100, message = "Cannot create more than 100 partyrooms at once")
    private Integer partyroomCount;

    /**
     * Number of virtual members to create per partyroom
     * One will be the HOST, others will be regular crew members
     */
    @NotNull(message = "Users per room count is required")
    @Min(value = 1, message = "Must have at least 1 user (HOST) per room")
    @Max(value = 100, message = "Cannot have more than 100 users per room")
    private Integer usersPerRoom;

    /**
     * Partyroom title prefix
     * Final title will be: "{titlePrefix} {number}"
     * e.g., "Preview Room 1", "Preview Room 2", etc.
     */
    @NotBlank(message = "Title prefix is required")
    private String titlePrefix;

    /**
     * Partyroom introduction (same for all rooms)
     */
    private String introduction;

    /**
     * Link domain prefix
     * Final link domain will be: "{linkDomainPrefix}_{number}"
     * If empty, will be auto-generated
     */
    private String linkDomainPrefix;

    /**
     * Playback time limit per track (in seconds)
     * Same for all rooms
     */
    @NotNull(message = "Playback time limit is required")
    @Min(value = 1, message = "Playback time limit must be at least 1 second")
    private Integer playbackTimeLimit;
}
