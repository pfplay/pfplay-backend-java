package com.pfplaybackend.api.user.domain.service;

import com.pfplaybackend.api.user.domain.enums.FaceSourceType;
import com.pfplaybackend.api.user.adapter.in.web.payload.request.SetAvatarRequest;
import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.user.application.dto.shared.AvatarIconDto;
import com.pfplaybackend.api.user.application.service.AvatarResourceService;
import com.pfplaybackend.api.user.domain.entity.data.ActivityData;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.domain.enums.ObtainmentType;
import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import com.pfplaybackend.api.user.domain.value.AvatarIconUri;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserAvatarDomainService {
    private final AvatarResourceService avatarResourceService;

    public boolean isAvailableBody(AvatarBodyDto avatarBodyDto, Map<ActivityType, ActivityData> activityMap) {
        ObtainmentType obtainmentType = avatarBodyDto.getObtainableType();
        if(obtainmentType.equals(ObtainmentType.BASIC)) return true;
        // Comparison of Scores
        ActivityType activityType = convertToActivityType(avatarBodyDto.getObtainableType());
        return activityMap.get(activityType).getScore().isAtLeast(avatarBodyDto.getObtainableScore());
    }

    private ActivityType convertToActivityType(ObtainmentType obtainmentType) {
        switch (obtainmentType) {
            case DJ_PNT -> {
                return ActivityType.DJ_PNT;
            }
            case REF_LINK -> {
                return ActivityType.REF_LINK;
            }
            case ROOM_ACT -> {
                return ActivityType.ROOM_ACT;
            }
            default -> throw new IllegalArgumentException("Unsupported ObtainmentType: " + obtainmentType);
        }
    }

    public AvatarIconUri findAvatarIconPairWithSingleBody(AvatarBodyDto avatarBodyDto) {
        AvatarIconDto avatarIconDto = avatarResourceService.findPairAvatarIconByBodyUri(new AvatarBodyUri(avatarBodyDto.getResourceUri()));
        return new AvatarIconUri(avatarIconDto.resourceUri());
    }

    public AvatarIconUri findAvatarIconByFaceSourceType(SetAvatarRequest request) {
        AvatarFaceUri avatarFaceUri = new AvatarFaceUri(request.getFace().getUri());
        if(request.getFace().getSourceType().equals(FaceSourceType.INTERNAL_IMAGE)) {
            AvatarIconDto avatarIconDto = avatarResourceService.findPairAvatarIconByFaceUri(avatarFaceUri);
            return new AvatarIconUri(avatarIconDto.resourceUri());
        }else {
            return new AvatarIconUri(avatarFaceUri.getAvatarFaceUri());
        }
    }
}
