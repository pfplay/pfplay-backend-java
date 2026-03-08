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
    private final boolean combinable;
    private final boolean defaultSetting;
    private final boolean available;
    private final int combinePositionX;
    private final int combinePositionY;

    public static AvatarBodyDto create(AvatarBodyResourceData avatarBodyResource) {
        return AvatarBodyDto.builder()
                .id(avatarBodyResource.getId())
                .name(avatarBodyResource.getName())
                .resourceUri(avatarBodyResource.getResourceUri())
                .obtainableType(avatarBodyResource.getObtainableType())
                .obtainableScore(avatarBodyResource.getObtainableScore())
                .combinable(avatarBodyResource.isCombinable())
                .defaultSetting(avatarBodyResource.isDefaultSetting())
                // BASIC 타입인 경우 available 한 것으로 처리
                .available(avatarBodyResource.getObtainableType().equals(ObtainmentType.BASIC))
                .combinePositionX(avatarBodyResource.getCombinePositionX())
                .combinePositionY(avatarBodyResource.getCombinePositionY())
                .build();
    }
}
