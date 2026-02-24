package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.user.adapter.out.persistence.MemberRepository;
import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.user.application.dto.shared.AvatarFaceDto;
import com.pfplaybackend.api.user.application.dto.shared.AvatarIconDto;
import com.pfplaybackend.api.user.domain.entity.data.ActivityData;
import com.pfplaybackend.api.user.domain.entity.data.AvatarBodyResourceData;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.domain.service.UserAvatarDomainService;
import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import com.pfplaybackend.api.user.domain.value.AvatarIconUri;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAvatarQueryService {

    private final MemberRepository memberRepository;
    private final UserAvatarDomainService userAvatarDomainService;
    private final AvatarResourceQueryService avatarResourceQueryService;

    public AvatarBodyResourceData getDefaultAvatarBodyResourceData() {
        return avatarResourceQueryService.getDefaultSettingResourceAvatarBody();
    }

    @Transactional(readOnly = true)
    public AvatarBodyUri getDefaultAvatarBodyUri() {
        AvatarBodyResourceData data = avatarResourceQueryService.getDefaultSettingResourceAvatarBody();
        return new AvatarBodyUri(data.getResourceUri());
    }

    public AvatarFaceUri getDefaultAvatarFaceUri() {
        return new AvatarFaceUri(avatarResourceQueryService.findAllAvatarFaces().get(0).resourceUri());
    }

    public AvatarIconUri getDefaultAvatarIconUri() {
        AvatarIconDto avatarIconDto = avatarResourceQueryService.findPairAvatarIconByFaceUri(this.getDefaultAvatarFaceUri());
        return new AvatarIconUri(avatarIconDto.resourceUri());
    }

    public List<AvatarFaceDto> findMyAvatarFaces() {
        return avatarResourceQueryService.findAllAvatarFaces();
    }

    @Transactional(readOnly = true)
    public List<AvatarBodyDto> findMyAvatarBodies() {
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        List<AvatarBodyDto> avatarBodyDtoList = avatarResourceQueryService.findAllAvatarBodies();
        if (authContext.getAuthorityTier() == AuthorityTier.GT) {
            return avatarBodyDtoList;
        } else {
            MemberData member = memberRepository.findByUserId(authContext.getUserId()).orElseThrow();
            Map<ActivityType, ActivityData> activityMap = member.getActivityDataMap();
            return avatarBodyDtoList.stream()
                    .map(avatarBodyDto -> avatarBodyDto.toBuilder()
                            .isAvailable(userAvatarDomainService.isAvailableBody(
                                    avatarBodyDto.getObtainableType(), avatarBodyDto.getObtainableScore(), activityMap))
                            .build()
                    ).toList();
        }
    }
}
