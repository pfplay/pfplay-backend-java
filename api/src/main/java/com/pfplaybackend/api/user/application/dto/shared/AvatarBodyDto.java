package com.pfplaybackend.api.user.application.dto.shared;

import com.pfplaybackend.api.user.domain.model.domain.AvatarResource;
import com.pfplaybackend.api.user.domain.model.enums.ObtainmentType;
import lombok.*;

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

    static public AvatarBodyDto create(AvatarResource avatarResource) {
        return AvatarBodyDto.builder()
                .id(avatarResource.getId())
                .name(avatarResource.getName())
                .resourceUri(avatarResource.getResourceUri())
                .obtainableType(avatarResource.getObtainableType())
                .obtainableScore(avatarResource.getObtainableScore())
                .isCombinable(avatarResource.isCombinable())
                .isDefaultSetting(avatarResource.isDefaultSetting())
                .isAvailable(avatarResource.getObtainableType().equals(ObtainmentType.BASIC))
                .build();
    }
}
