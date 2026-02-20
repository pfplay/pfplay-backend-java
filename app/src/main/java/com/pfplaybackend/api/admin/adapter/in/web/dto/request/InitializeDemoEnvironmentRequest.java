package com.pfplaybackend.api.admin.adapter.in.web.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request DTO for initializing complete demo environment
 *
 * Creates:
 * - 410 virtual members (13 special with playlists + 397 regular)
 * - 13 partyrooms (1 main stage + 12 general rooms)
 * - Enters members into rooms (50 in main, 30 each in general)
 * - Registers 1 DJ per room from special accounts
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InitializeDemoEnvironmentRequest {

    /**
     * Playback time limit for all partyrooms (in seconds)
     * Default: 300 (5 minutes)
     */
    @NotNull(message = "Playback time limit is required")
    @Min(value = 1, message = "Playback time limit must be at least 1 second")
    private Integer playbackTimeLimit;

    /**
     * Title prefix for general partyrooms
     * Main stage will always be "Main Stage"
     * General rooms will be: "{titlePrefix} 1", "{titlePrefix} 2", etc.
     * Default: "Demo Room"
     */
    private String titlePrefix;

    /**
     * Introduction text for all partyrooms
     * Default: "Demo environment for preview and testing"
     */
    private String introduction;

    /**
     * Whether to register DJs in queue
     * If true, registers 1 DJ per room with their playlist
     * Default: true
     */
    private Boolean registerDjs;

    public String getTitlePrefix() {
        return titlePrefix != null && !titlePrefix.isEmpty() ? titlePrefix : "Demo Room";
    }

    public String getIntroduction() {
        return introduction != null && !introduction.isEmpty()
                ? introduction
                : "Demo environment for preview and testing";
    }

    public Boolean getRegisterDjs() {
        return registerDjs != null ? registerDjs : true;
    }
}
