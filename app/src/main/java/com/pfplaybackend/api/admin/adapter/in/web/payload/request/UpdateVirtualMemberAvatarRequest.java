package com.pfplaybackend.api.admin.adapter.in.web.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating virtual member's avatar
 */
@Getter
@NoArgsConstructor
public class UpdateVirtualMemberAvatarRequest {

    /**
     * Avatar body URI (required)
     * Full URL to avatar body resource
     */
    @NotBlank(message = "Avatar body URI is required")
    private String avatarBodyUri;

    /**
     * Avatar face URI (optional)
     * Empty string for SINGLE_BODY type
     */
    private String avatarFaceUri;
}
