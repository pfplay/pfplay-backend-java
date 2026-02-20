package com.pfplaybackend.api.admin.adapter.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pfplaybackend.api.common.config.security.enums.ProviderType;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Response DTO for virtual member information
 */
@Getter
@Builder
public class VirtualMemberResponse {

    /**
     * User ID (UUID format)
     */
    private String userId;

    /**
     * Email (virtual_{uuid}@pfplay.system format)
     */
    private String email;

    /**
     * Nickname
     */
    private String nickname;

    /**
     * Introduction (bio)
     */
    private String introduction;

    /**
     * Provider type (always ADMIN for virtual members)
     */
    private ProviderType providerType;

    /**
     * Authority tier (always FM for virtual members)
     */
    private AuthorityTier authorityTier;

    /**
     * Avatar body URI
     */
    private String avatarBodyUri;

    /**
     * Avatar face URI
     */
    private String avatarFaceUri;

    /**
     * Avatar icon URI
     */
    private String avatarIconUri;

    /**
     * Avatar composition type
     */
    private AvatarCompositionType avatarCompositionType;

    /**
     * Combine position X
     */
    private Integer combinePositionX;

    /**
     * Combine position Y
     */
    private Integer combinePositionY;

    /**
     * Offset X for face
     */
    private Double offsetX;

    /**
     * Offset Y for face
     */
    private Double offsetY;

    /**
     * Scale for face
     */
    private Double scale;

    /**
     * Created timestamp
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * Updated timestamp
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
