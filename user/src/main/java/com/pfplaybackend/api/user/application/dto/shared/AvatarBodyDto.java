package com.pfplaybackend.api.user.application.dto.shared;

import com.pfplaybackend.api.user.domain.entity.data.AvatarBodyResourceData;
import com.pfplaybackend.api.user.domain.enums.ObtainmentType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
@Getter
@Builder(toBuilder = true)
@ToString
public class AvatarBodyDto {
    private Long id;
    private final String name;
    private final String resourceUri;
    private final ObtainmentType obtainableType;
    private final int obtainableScore;
    private final boolean isCombinable;
    private final boolean isDefaultSetting;
    private final boolean isAvailable;
    private final int combinePositionX;
    private final int combinePositionY;

    static public AvatarBodyDto create(AvatarBodyResourceData avatarBodyResource) {
        return AvatarBodyDto.builder()
                .id(avatarBodyResource.getId())
                .name(avatarBodyResource.getName())
                .resourceUri(avatarBodyResource.getResourceUri())
                .obtainableType(avatarBodyResource.getObtainableType())
                .obtainableScore(avatarBodyResource.getObtainableScore())
                .isCombinable(avatarBodyResource.isCombinable())
                .isDefaultSetting(avatarBodyResource.isDefaultSetting())
                // BASIC 타입인 경우 isAvailable 한 것으로 처리
                .isAvailable(avatarBodyResource.getObtainableType().equals(ObtainmentType.BASIC))
                .combinePositionX(avatarBodyResource.getCombinePositionX())
                .combinePositionY(avatarBodyResource.getCombinePositionY())
                .build();
    }
}
