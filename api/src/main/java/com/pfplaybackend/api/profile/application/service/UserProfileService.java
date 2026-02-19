package com.pfplaybackend.api.profile.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.profile.domain.ProfileData;
import com.pfplaybackend.api.profile.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.profile.domain.enums.FaceSourceType;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import com.pfplaybackend.api.user.domain.entity.data.AvatarBodyResourceData;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.service.GuestDomainService;
import com.pfplaybackend.api.user.domain.entity.data.GuestData;
import com.pfplaybackend.api.user.domain.value.UserId;
import com.pfplaybackend.api.user.domain.service.UserDomainService;
import com.pfplaybackend.api.user.adapter.out.persistence.GuestRepository;
import com.pfplaybackend.api.user.adapter.out.persistence.MemberRepository;
import com.pfplaybackend.api.profile.adapter.out.persistence.UserProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final GuestRepository guestRepository;
    private final MemberRepository memberRepository;
    private final UserDomainService userDomainService;
    private final GuestDomainService guestDomainService;
    private final UserAvatarService userAvatarService;

    public ProfileData createProfileDataForGuest(UserId userId) {
        AvatarBodyResourceData avatarBodyResource = userAvatarService.getDefaultAvatarBodyResourceData();
        return ProfileData.builder()
                .userId(userId)
                .nickname(guestDomainService.generateRandomNickname())
                .avatarCompositionType(AvatarCompositionType.BODY_WITH_FACE)
                .faceSourceType(FaceSourceType.INTERNAL_IMAGE)
                .avatarBodyUri(userAvatarService.getDefaultAvatarBodyUri())
                .avatarFaceUri(userAvatarService.getDefaultAvatarFaceUri())
                .avatarIconUri(userAvatarService.getDefaultAvatarIconUri())
                .combinePositionX(avatarBodyResource.getCombinePositionX())
                .combinePositionY(avatarBodyResource.getCombinePositionY())
                .build();
    }

    public ProfileData createProfileDataForMember(UserId userId) {
        return ProfileData.builder()
                .userId(userId)
                .build();
    }

    public ProfileSummaryDto getMyProfileSummary() {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        if(userDomainService.isGuest(authContext)) {
            GuestData guestData = guestRepository.findByUserId(authContext.getUserId()).orElseThrow();
            return guestData.getProfileSummary();
        }else {
            MemberData memberData = memberRepository.findByUserId(authContext.getUserId()).orElseThrow();
            return memberData.getProfileSummary();
        }
    }

    public ProfileSummaryDto getOtherProfileSummary(UserId otherUserId, AuthorityTier authorityTier) {
        if(userDomainService.isGuest(authorityTier)) {
            GuestData guestData = guestRepository.findByUserId(otherUserId).orElseThrow();
            return guestData.getProfileSummary();
        }else {
            MemberData memberData = memberRepository.findByUserId(otherUserId).orElseThrow();
            return memberData.getProfileSummary();
        }
    }

    // 다수 사용자에 대한 프로필 설정 정보 조회
    @Transactional
    public Map<UserId, ProfileSettingDto> getUsersProfileSetting(List<UserId> userIds) {
        List<ProfileData> list = userProfileRepository.findAllByUserIdIn(userIds);
        return userProfileRepository.findAllByUserIdIn(userIds).stream()
                .collect(Collectors.toMap(ProfileData::getUserId, profileData ->
                        new ProfileSettingDto(profileData.getNickname(),
                                profileData.getAvatarCompositionType(),
                                profileData.getAvatarBodyUri().getAvatarBodyUri(),
                                profileData.getAvatarFaceUri().getAvatarFaceUri(),
                                profileData.getAvatarIconUri().getAvatarIconUri(),
                                profileData.getCombinePositionX(),
                                profileData.getCombinePositionY(),
                                profileData.getOffsetX(),
                                profileData.getOffsetY(),
                                profileData.getScale()
                        )
                ));
    }

    // 특정 사용자에 대한 프로필 설정 정보 조회
    @Transactional
    public ProfileSettingDto getUserProfileSetting(UserId userId) {
        ProfileData profileData = userProfileRepository.findByUserId(userId);
        return new ProfileSettingDto(profileData.getNickname(),
                profileData.getAvatarCompositionType(),
                profileData.getAvatarBodyUri().getAvatarBodyUri(),
                profileData.getAvatarFaceUri().getAvatarFaceUri(),
                profileData.getAvatarIconUri().getAvatarIconUri(),
                profileData.getCombinePositionX(),
                profileData.getCombinePositionY(),
                profileData.getOffsetX(),
                profileData.getOffsetY(),
                profileData.getScale()
        );
    }
}
