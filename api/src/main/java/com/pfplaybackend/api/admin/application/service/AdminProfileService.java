package com.pfplaybackend.api.admin.application.service;

import com.pfplaybackend.api.avatarresource.application.service.AvatarResourceService;
import com.pfplaybackend.api.profile.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.profile.domain.enums.FaceSourceType;
import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Profile;
import com.pfplaybackend.api.user.domain.service.UserAvatarDomainService;
import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import com.pfplaybackend.api.user.domain.value.AvatarIconUri;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service for creating and managing profiles for virtual (admin-created) members
 */
@Service
@RequiredArgsConstructor
public class AdminProfileService {

    private final AvatarResourceService avatarResourceService;
    private final UserAvatarDomainService userAvatarDomainService;

    /**
     * Create profile for virtual member with auto-generated nickname and default avatar
     * Similar to Guest profile creation but with "Virtual_" prefix
     *
     * @param userId User ID
     * @return Initialized profile with default values
     */
    public Profile createProfileForVirtualMember(UserId userId) {
        return createProfileForVirtualMember(userId, null, null, null);
    }

    /**
     * Create profile for virtual member with specified nickname and/or custom avatar
     *
     * @param userId        User ID
     * @param nickname      Optional nickname (auto-generated if null)
     * @param avatarBodyUri Optional avatar body URI (default if null)
     * @param avatarFaceUri Optional avatar face URI (default if null)
     * @return Initialized profile
     */
    public Profile createProfileForVirtualMember(
            UserId userId,
            String nickname,
            AvatarBodyUri avatarBodyUri,
            AvatarFaceUri avatarFaceUri) {

        // Generate nickname if not provided
        String finalNickname = (nickname != null && !nickname.isBlank())
                ? nickname
                : generateRandomNickname();

        // Get avatar URIs (use provided or default)
        AvatarBodyUri finalBodyUri = (avatarBodyUri != null)
                ? avatarBodyUri
                : getDefaultAvatarBodyUri();

        AvatarFaceUri finalFaceUri = (avatarFaceUri != null)
                ? avatarFaceUri
                : new AvatarFaceUri();  // Empty for SINGLE_BODY type

        // Get avatar body info for position values
        AvatarBodyDto avatarBodyDto = avatarResourceService.findAvatarBodyByUri(finalBodyUri);

        // Auto-detect composition type and face source type from face URI pattern
        AvatarCompositionType compositionType;
        FaceSourceType faceSourceType;

        if (finalFaceUri.getAvatarFaceUri() != null && finalFaceUri.getAvatarFaceUri().contains("ava_nft_tmp")) {
            // NFT face pattern detected → BODY_WITH_FACE
            compositionType = AvatarCompositionType.BODY_WITH_FACE;
            faceSourceType = FaceSourceType.NFT_URI;
        } else if (finalFaceUri.getAvatarFaceUri() == null || finalFaceUri.getAvatarFaceUri().isEmpty()) {
            // Empty face URI → SINGLE_BODY
            compositionType = AvatarCompositionType.SINGLE_BODY;
            faceSourceType = FaceSourceType.INTERNAL_IMAGE;
        } else {
            // Internal face image from DB → BODY_WITH_FACE
            compositionType = AvatarCompositionType.BODY_WITH_FACE;
            faceSourceType = FaceSourceType.INTERNAL_IMAGE;
        }

        // Determine icon based on composition type
        AvatarIconUri iconUri;
        if (compositionType == AvatarCompositionType.SINGLE_BODY) {
            // SINGLE_BODY: Use body-paired icon
            iconUri = userAvatarDomainService.findAvatarIconPairWithSingleBody(avatarBodyDto);
        } else if (faceSourceType == FaceSourceType.NFT_URI) {
            // BODY_WITH_FACE with NFT: NFT face URI becomes icon URI
            iconUri = new AvatarIconUri(finalFaceUri.getAvatarFaceUri());
        } else {
            // BODY_WITH_FACE with INTERNAL_IMAGE: Use face-paired icon
            iconUri = new AvatarIconUri(
                    avatarResourceService.findPairAvatarIconByFaceUri(finalFaceUri).getResourceUri()
            );
        }

        // Build profile with detected composition type
        return new Profile(userId)
                .withNickname(finalNickname)
                .withAvatarCompositionType(compositionType)
                .withFaceSourceType(faceSourceType)
                .withAvatarBodyUri(finalBodyUri)
                .withAvatarFaceUri(finalFaceUri)
                .withAvatarIconUri(iconUri)
                .withCombinePositionX(avatarBodyDto.getCombinePositionX())
                .withCombinePositionY(avatarBodyDto.getCombinePositionY())
                .withOffsetX(0)
                .withOffsetY(0)
                .withScale(1.0);
    }

    /**
     * Generate random nickname for virtual member
     * Pattern: Virtual_{6-char-hex}
     *
     * @return Generated nickname
     */
    private String generateRandomNickname() {
        String randomHex = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();

        return "Virtual_" + randomHex;
    }

    /**
     * Get default avatar body URI
     * Uses the first available avatar body resource
     *
     * @return Default avatar body URI
     */
    private AvatarBodyUri getDefaultAvatarBodyUri() {
        // Get first available avatar body (you might want to specify a particular default)
        return new AvatarBodyUri("https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_basic%2Fava_basic_001.png?alt=media");
    }
}
