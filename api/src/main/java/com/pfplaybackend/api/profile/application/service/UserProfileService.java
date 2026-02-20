package com.pfplaybackend.api.profile.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.profile.domain.ProfileData;
import com.pfplaybackend.api.profile.domain.value.AvatarSetting;
import com.pfplaybackend.api.profile.domain.value.Nickname;
import com.pfplaybackend.api.profile.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.profile.domain.enums.FaceSourceType;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import com.pfplaybackend.api.user.domain.entity.data.AvatarBodyResourceData;
import com.pfplaybackend.api.user.domain.entity.data.UserAccountData;
import com.pfplaybackend.api.user.domain.service.GuestDomainService;
import com.pfplaybackend.api.common.domain.value.UserId;
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
    private final GuestDomainService guestDomainService;
    private final UserAvatarService userAvatarService;

    public ProfileData createProfileDataForGuest(UserId userId) {
        AvatarBodyResourceData avatarBodyResource = userAvatarService.getDefaultAvatarBodyResourceData();
        return ProfileData.builder()
                .userId(userId)
                .nickname(new Nickname(guestDomainService.generateRandomNickname()))
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
        return findUserWithProfile(authContext.getUserId(), authContext.getAuthorityTier())
                .getProfileSummary();
    }

    public ProfileSummaryDto getOtherProfileSummary(UserId otherUserId, AuthorityTier authorityTier) {
        return findUserWithProfile(otherUserId, authorityTier)
                .getProfileSummary();
    }

    private UserAccountData findUserWithProfile(UserId userId, AuthorityTier tier) {
        if (tier == AuthorityTier.GT) {
            return guestRepository.findByUserId(userId).orElseThrow();
        }
        return memberRepository.findByUserId(userId).orElseThrow();
    }

    // 다수 사용자에 대한 프로필 설정 정보 조회
    @Transactional
    public Map<UserId, ProfileSettingDto> getUsersProfileSetting(List<UserId> userIds) {
        return userProfileRepository.findAllByUserIdIn(userIds).stream()
                .collect(Collectors.toMap(ProfileData::getUserId, this::toProfileSettingDto));
    }

    // 특정 사용자에 대한 프로필 설정 정보 조회
    @Transactional
    public ProfileSettingDto getUserProfileSetting(UserId userId) {
        ProfileData profileData = userProfileRepository.findByUserId(userId);
        return toProfileSettingDto(profileData);
    }

    private ProfileSettingDto toProfileSettingDto(ProfileData profileData) {
        AvatarSetting avatar = profileData.getAvatarSetting();
        return new ProfileSettingDto(
                profileData.getNicknameValue(),
                avatar.getAvatarCompositionType(),
                avatar.getAvatarBodyUri().getAvatarBodyUri(),
                avatar.getAvatarFaceUri().getAvatarFaceUri(),
                avatar.getAvatarIconUri().getAvatarIconUri(),
                avatar.getCombinePositionX(),
                avatar.getCombinePositionY(),
                avatar.getOffsetX(),
                avatar.getOffsetY(),
                avatar.getScale()
        );
    }
}
