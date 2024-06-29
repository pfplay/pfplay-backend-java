package com.pfplaybackend.api.user.application.dto.shared;

import com.pfplaybackend.api.user.domain.entity.domainmodel.AvatarResource;
import com.pfplaybackend.api.user.domain.enums.ObtainmentType;
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
    private final int x;
    private final int y;

    static public AvatarBodyDto create(AvatarResource avatarResource) {
        return AvatarBodyDto.builder()
                .id(avatarResource.getId())
                .name(avatarResource.getName())
                .resourceUri(avatarResource.getResourceUri())
                .obtainableType(avatarResource.getObtainableType())
                .obtainableScore(avatarResource.getObtainableScore())
                .isCombinable(avatarResource.isCombinable())
                .isDefaultSetting(avatarResource.isDefaultSetting())
                // BASIC 타입인 경우 isAvailable 한 것으로 처리
                .isAvailable(avatarResource.getObtainableType().equals(ObtainmentType.BASIC))
                .x(avatarResource.getX())
                .y(avatarResource.getY())
                .build();
    }
}
