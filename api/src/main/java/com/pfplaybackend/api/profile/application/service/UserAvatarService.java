package com.pfplaybackend.api.profile.application.service;

import com.pfplaybackend.api.avatarresource.application.service.AvatarResourceService;
import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.profile.application.event.UserProfileEventService;
import com.pfplaybackend.api.profile.domain.ProfileData;
import com.pfplaybackend.api.profile.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.profile.domain.enums.FaceSourceType;
import com.pfplaybackend.api.profile.domain.repository.UserProfileRepository;
import com.pfplaybackend.api.profile.domain.vo.*;
import com.pfplaybackend.api.profile.presentation.dto.request.AvatarFaceRequest;
import com.pfplaybackend.api.profile.presentation.dto.request.SetAvatarRequest;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.user.application.dto.command.UpdateAvatarBodyCommand;
import com.pfplaybackend.api.user.application.dto.command.UpdateAvatarFaceCommand;
import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.user.application.dto.shared.AvatarFaceDto;
import com.pfplaybackend.api.user.application.dto.shared.AvatarIconDto;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Activity;
import com.pfplaybackend.api.user.domain.entity.domainmodel.AvatarBodyResource;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Member;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.domain.enums.ObtainmentType;
import com.pfplaybackend.api.user.domain.exception.UserAvatarException;
import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import com.pfplaybackend.api.user.domain.service.UserAvatarDomainService;
import com.pfplaybackend.api.user.domain.service.UserDomainService;
import com.pfplaybackend.api.user.domain.value.AvatarIconUri;
import com.pfplaybackend.api.user.repository.MemberRepository;
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

    private final UserProfileRepository userProfileRepository;

    // TODO Call AvatarResourceService In 'Other Sub Domain'
    private final MemberRepository memberRepository;
    private final UserDomainService userDomainService;
    private final UserAvatarDomainService userAvatarDomainService;
    // Using Peer Service
    private final AvatarResourceService avatarResourceService;
    // Event
    private final UserProfileEventService userProfileEventService;

    public AvatarBodyResource getDefaultAvatarBodyResource() {
        return avatarResourceService.getDefaultSettingResourceAvatarBody();
    }

    @Transactional(readOnly = true)
    public AvatarBodyUri getDefaultAvatarBodyUri() {
        AvatarBodyResource avatarBodyResource = avatarResourceService.getDefaultSettingResourceAvatarBody();
        return new AvatarBodyUri(avatarBodyResource.getResourceUri());
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
        if (userDomainService.isGuest(authContext)) {
            return avatarBodyDtoList;
        } else {
            Member member = memberRepository.findByUserId(authContext.getUserId()).orElseThrow().toDomain();
            Map<ActivityType, Activity> activityMap = member.getActivityMap();
            return avatarBodyDtoList.stream()
                    .map(avatarBodyDto -> avatarBodyDto.toBuilder()
                            .isAvailable(userAvatarDomainService.isAvailableBody(avatarBodyDto, activityMap))
                            .build()
                    ).toList();
        }
    }

    @Transactional
    public void setUserAvatar(SetAvatarRequest request) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        Member member = memberRepository.findByUserId(authContext.getUserId()).orElseThrow().toDomain();

        // 0. 리소스 접근 권한 유효성 검증
        AvatarBodyDto avatarBodyDto = avatarResourceService.findAvatarBodyByUri(new AvatarBodyUri(request.getBody().getUri()));
        if (!avatarBodyDto.getObtainableType().equals(ObtainmentType.BASIC)) {
            ActivityType activityType = ActivityType.of(avatarBodyDto.getObtainableType());
            Activity activity = member.getActivityMap().get(activityType);
            if (activity.getScore() < avatarBodyDto.getObtainableScore()) {
                throw ExceptionCreator.create(UserAvatarException.AVATAR_SELECTION_FORBIDDEN);
            }
        }

        AvatarFaceUri avatarFaceUri;
        AvatarIconUri avatarIconUri;
        Member updatedMember = member.updateAvatarBody(avatarBodyDto);

        if(request.getAvatarCompositionType().equals(AvatarCompositionType.SINGLE_BODY)) {
            avatarFaceUri = new AvatarFaceUri();
            avatarIconUri = userAvatarDomainService.findAvatarIconPairWithSingleBody(avatarBodyDto);

            updatedMember = updatedMember.updateAvatarFace(avatarFaceUri)
                    .updateAvatarIcon(avatarIconUri);
        }else {
            avatarFaceUri = new AvatarFaceUri(request.getFace().getUri());
            avatarIconUri = userAvatarDomainService.findAvatarIconByFaceSourceType(request);

            updatedMember = updatedMember.updateAvatarFace(avatarFaceUri, request.getFace())
                    .updateAvatarIcon(avatarIconUri);
        }

        memberRepository.save(updatedMember.toData());
        userProfileEventService.publishProfileChangedEvent(updatedMember);
    }

//    private Avatar createAvatarFromRequest(SetAvatarRequest request) {
//        AvatarBody body = AvatarBody.of(request.getBody().getUri());
//        return switch (request.getAvatarCompositionType()) {
//            case SINGLE_BODY -> Avatar.singleBody(body);
//            case BODY_WITH_FACE -> {
//                AvatarFace face = createAvatarFaceFromRequest(request.getFace());
//                yield Avatar.bodyWithFace(body, face);
//            }
//        };
//    }
//
//    private AvatarFace createAvatarFaceFromRequest(AvatarFaceRequest faceRequest) {
//        FaceTransform transform = FaceTransform.of(
//                faceRequest.getTransform().getOffsetX(),
//                faceRequest.getTransform().getOffsetY(),
//                faceRequest.getTransform().getScale()
//        );
//
//        return AvatarFace.of(
//                faceRequest.getSourceType(),
//                faceRequest.getUri(),
//                transform
//        );
//    }
}