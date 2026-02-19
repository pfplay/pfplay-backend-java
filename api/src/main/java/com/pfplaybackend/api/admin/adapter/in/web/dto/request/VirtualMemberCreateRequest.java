package com.pfplaybackend.api.admin.adapter.in.web.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a virtual member
 */
@Getter
@NoArgsConstructor
public class VirtualMemberCreateRequest {

    /**
     * Optional nickname for the virtual member
     * If not provided, will be auto-generated as "Virtual_{random}"
     */
    @Size(max = 20, message = "Nickname must be less than 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣_]+$",
            message = "Nickname can only contain letters, numbers, and underscores")
    private String nickname;

    /**
     * Optional avatar body URI
     * If not provided, will use default avatar
     */
    private String avatarBodyUri;

    /**
     * Optional avatar face URI
     * If not provided, will use empty/default face
     */
    private String avatarFaceUri;
}
