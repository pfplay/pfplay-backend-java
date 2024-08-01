package com.pfplaybackend.api.user.domain.service;

import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.user.application.dto.shared.AvatarIconDto;
import com.pfplaybackend.api.user.application.service.AvatarResourceService;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Activity;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Member;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.domain.enums.ObtainmentType;
import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import com.pfplaybackend.api.user.domain.value.AvatarIconUri;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.BinaryOperator;

@Service
@RequiredArgsConstructor
public class UserAvatarDomainService {
    private final AvatarResourceService avatarResourceService;

    public boolean isAvailableBody(AvatarBodyDto avatarBodyDto, Map<ActivityType, Activity> activityMap) {
        ObtainmentType obtainmentType = avatarBodyDto.getObtainableType();
        if(obtainmentType.equals(ObtainmentType.BASIC)) return true;
        // Comparison of Scores
        ActivityType activityType = convertToActivityType(avatarBodyDto.getObtainableType());
        return activityMap.get(activityType).getScore() >= avatarBodyDto.getObtainableScore();
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
        }
        return null;
    }

    public AvatarFaceUri updateFaceUriOnBodyUriChange(Member member, AvatarBodyDto avatarBodyDto) {
        return avatarBodyDto.isCombinable() ? member.getProfile().getAvatarFaceUri() : new AvatarFaceUri();
    }

    public AvatarIconUri updateIconUriOnBodyUriChange(Member member, AvatarBodyDto avatarBodyDto) {
        if(avatarBodyDto.isCombinable()) {
            return member.getProfile().getAvatarIconUri();
        }else {
            AvatarIconDto avatarIconDto = avatarResourceService.findPairAvatarIconByBodyUri(new AvatarBodyUri(avatarBodyDto.getResourceUri()));
            return new AvatarIconUri(avatarIconDto.getResourceUri());
        }
    }

    public AvatarIconUri updateIconUriOnFaceUriChange(Member member, AvatarFaceUri avatarFaceUri) {
        if(avatarResourceService.isBasicFaceUri(avatarFaceUri)) {
            AvatarIconDto avatarIconDto = avatarResourceService.findPairAvatarIconByFaceUri(avatarFaceUri);
            return new AvatarIconUri(avatarIconDto.getResourceUri());
        }else {
            // Return equal uri
            return new AvatarIconUri(avatarFaceUri.getAvatarFaceUri());
        }
    }
}
