package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.user.adapter.out.persistence.GuestRepository;
import com.pfplaybackend.api.user.adapter.out.persistence.MemberRepository;
import com.pfplaybackend.api.user.adapter.out.persistence.UserProfileRepository;
import com.pfplaybackend.api.user.application.dto.shared.ActivitySummaryDto;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import com.pfplaybackend.api.user.domain.entity.data.ProfileData;
import com.pfplaybackend.api.user.domain.entity.data.UserAccountData;
import com.pfplaybackend.api.user.domain.value.AvatarSetting;
import com.pfplaybackend.api.user.domain.value.ProfileSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileQueryService {
    private final UserProfileRepository userProfileRepository;
    private final GuestRepository guestRepository;
    private final MemberRepository memberRepository;

    public ProfileSummaryDto getMyProfileSummary() {
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        ProfileSummary summary = findUserWithProfile(authContext.getUserId(), authContext.getAuthorityTier())
                .getProfileSummary();
        return toProfileSummaryDto(summary);
    }

    public ProfileSummaryDto getOtherProfileSummary(UserId otherUserId, AuthorityTier authorityTier) {
        ProfileSummary summary = findUserWithProfile(otherUserId, authorityTier)
                .getProfileSummary();
        return toProfileSummaryDto(summary);
    }

    public AuthorityTier getAuthorityTier(UserId userId) {
        return memberRepository.findByUserId(userId)
                .map(UserAccountData::getAuthorityTier)
                .orElseGet(() -> guestRepository.findByUserId(userId)
                        .map(UserAccountData::getAuthorityTier)
                        .orElseThrow());
    }

    private UserAccountData findUserWithProfile(UserId userId, AuthorityTier tier) {
        if (tier == AuthorityTier.GT) {
            return guestRepository.findByUserId(userId).orElseThrow();
        }
        return memberRepository.findByUserId(userId).orElseThrow();
    }

    // 다수 사용자에 대한 프로필 설정 정보 조회
    @Transactional(readOnly = true)
    public Map<UserId, ProfileSettingDto> getUsersProfileSetting(List<UserId> userIds) {
        return userProfileRepository.findAllByUserIdIn(userIds).stream()
                .collect(Collectors.toMap(ProfileData::getUserId, this::toProfileSettingDto));
    }

    // 특정 사용자에 대한 프로필 설정 정보 조회
    @Transactional(readOnly = true)
    public ProfileSettingDto getUserProfileSetting(UserId userId) {
        ProfileData profileData = userProfileRepository.findByUserId(userId);
        return toProfileSettingDto(profileData);
    }

    private ProfileSummaryDto toProfileSummaryDto(ProfileSummary summary) {
        return new ProfileSummaryDto(
                summary.nickname(),
                summary.introduction(),
                summary.avatarBodyUri(),
                summary.avatarCompositionType(),
                summary.combinePositionX(),
                summary.combinePositionY(),
                summary.offsetX(),
                summary.offsetY(),
                summary.scale(),
                summary.avatarFaceUri(),
                summary.avatarIconUri(),
                summary.walletAddress(),
                summary.activitySummaries() != null
                        ? summary.activitySummaries().stream()
                            .map(a -> new ActivitySummaryDto(a.activityType(), a.score()))
                            .toList()
                        : List.of()
        );
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
