package com.pfplaybackend.api.profile.application.service;

import com.pfplaybackend.api.avatarresource.application.service.AvatarResourceService;
import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.profile.application.event.UserProfileEventService;
import com.pfplaybackend.api.profile.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.user.application.dto.shared.AvatarFaceDto;
import com.pfplaybackend.api.user.application.dto.shared.AvatarIconDto;
import com.pfplaybackend.api.user.domain.entity.data.AvatarBodyResourceData;
import com.pfplaybackend.api.user.domain.entity.data.ActivityData;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.domain.enums.ObtainmentType;
import com.pfplaybackend.api.user.domain.exception.UserAvatarException;
import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import com.pfplaybackend.api.user.domain.service.UserAvatarDomainService;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.user.domain.value.AvatarIconUri;
import com.pfplaybackend.api.user.adapter.out.persistence.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAvatarService {

    // TODO Call AvatarResourceService In 'Other Sub Domain'
    private final MemberRepository memberRepository;
    private final UserAvatarDomainService userAvatarDomainService;
    // Using Peer Service
    private final AvatarResourceService avatarResourceService;
    // Event
    private final UserProfileEventService userProfileEventService;

    public AvatarBodyResourceData getDefaultAvatarBodyResourceData() {
        return avatarResourceService.getDefaultSettingResourceAvatarBody();
    }

    @Transactional(readOnly = true)
    public AvatarBodyUri getDefaultAvatarBodyUri() {
        AvatarBodyResourceData data = avatarResourceService.getDefaultSettingResourceAvatarBody();
        return new AvatarBodyUri(data.getResourceUri());
    }

    public AvatarFaceUri getDefaultAvatarFaceUri() {
        return new AvatarFaceUri(avatarResourceService.findAllAvatarFaces().get(0).getResourceUri());
    }

    public AvatarIconUri getDefaultAvatarIconUri() {
        AvatarIconDto avatarIconDto = avatarResourceService.findPairAvatarIconByFaceUri(this.getDefaultAvatarFaceUri());
        return new AvatarIconUri(avatarIconDto.getResourceUri());
    }

    public List<AvatarFaceDto> findMyAvatarFaces() {
        return avatarResourceService.findAllAvatarFaces();
    }

    @Transactional(readOnly = true)
    public List<AvatarBodyDto> findMyAvatarBodies() {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        List<AvatarBodyDto> avatarBodyDtoList = avatarResourceService.findAllAvatarBodies();
        if (authContext.getAuthorityTier() == AuthorityTier.GT) {
            return avatarBodyDtoList;
        } else {
            MemberData member = memberRepository.findByUserId(authContext.getUserId()).orElseThrow();
            Map<ActivityType, ActivityData> activityMap = member.getActivityDataMap();
            return avatarBodyDtoList.stream()
                    .map(avatarBodyDto -> avatarBodyDto.toBuilder()
                            .isAvailable(userAvatarDomainService.isAvailableBody(avatarBodyDto, activityMap))
                            .build()
                    ).toList();
        }
    }

    @Transactional
    public void setUserAvatar(com.pfplaybackend.api.profile.adapter.in.web.dto.request.SetAvatarRequest request) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        MemberData member = memberRepository.findByUserId(authContext.getUserId()).orElseThrow();

        // 0. 리소스 접근 권한 유효성 검증
        AvatarBodyDto avatarBodyDto = avatarResourceService.findAvatarBodyByUri(new AvatarBodyUri(request.getBody().getUri()));
        if (!avatarBodyDto.getObtainableType().equals(ObtainmentType.BASIC)) {
            ActivityType activityType = ActivityType.of(avatarBodyDto.getObtainableType());
            ActivityData activity = member.getActivityDataMap().get(activityType);
            if (!activity.getScore().isAtLeast(avatarBodyDto.getObtainableScore())) {
                throw ExceptionCreator.create(UserAvatarException.AVATAR_SELECTION_FORBIDDEN);
            }
        }

        AvatarFaceUri avatarFaceUri;
        AvatarIconUri avatarIconUri;
        member.updateAvatarBody(avatarBodyDto);

        if(request.getAvatarCompositionType().equals(AvatarCompositionType.SINGLE_BODY)) {
            avatarFaceUri = new AvatarFaceUri();
            avatarIconUri = userAvatarDomainService.findAvatarIconPairWithSingleBody(avatarBodyDto);

            member.updateAvatarFace(avatarFaceUri);
            member.updateAvatarIcon(avatarIconUri);
        }else {
            avatarFaceUri = new AvatarFaceUri(request.getFace().getUri());
            avatarIconUri = userAvatarDomainService.findAvatarIconByFaceSourceType(request);

            member.updateAvatarFace(avatarFaceUri, request.getFace());
            member.updateAvatarIcon(avatarIconUri);
        }

        memberRepository.save(member);
        userProfileEventService.publishProfileChangedEvent(member);
    }
}
