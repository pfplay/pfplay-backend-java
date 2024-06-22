package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import com.pfplaybackend.api.user.application.aspect.context.UserContext;
import com.pfplaybackend.api.user.application.dto.command.UpdateAvatarBodyCommand;
import com.pfplaybackend.api.user.application.dto.command.UpdateAvatarFaceCommand;
import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Activity;
import com.pfplaybackend.api.user.domain.entity.domainmodel.AvatarResource;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Member;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import com.pfplaybackend.api.user.domain.service.UserAvatarDomainService;
import com.pfplaybackend.api.user.domain.service.UserDomainService;
import com.pfplaybackend.api.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor

public class UserAvatarService {
    // TODO Call AvatarResourceService In 'Other Sub Domain'
    private final MemberRepository memberRepository;
    private final UserDomainService userDomainService;
    private final UserAvatarDomainService userAvatarDomainService;
    private final AvatarResourceService avatarResourceService;

    @Transactional(readOnly = true)
    public AvatarBodyUri getDefaultAvatarBodyUri() {
        AvatarResource avatarResource = avatarResourceService.getDefaultSettingResource();
        return new AvatarBodyUri(avatarResource.getResourceUri());
    }

    @Transactional(readOnly = true)
    public List<AvatarBodyDto> findMyAvatarBodies() {
        UserCredentials userCredentials = UserContext.getUserCredentials();
        List<AvatarBodyDto> avatarBodyDtoList = avatarResourceService.findAllAvatarBodies();

        if(userDomainService.isGuest(userCredentials)) {
            return avatarBodyDtoList;
        }else {
            Member member = memberRepository.findByUserId(userCredentials.getUserId()).orElseThrow().toDomain();
            Map<ActivityType, Activity> activityMap = member.getActivityMap();
            return avatarBodyDtoList.stream()
                    .map(avatarBodyDto -> avatarBodyDto.toBuilder()
                            .isAvailable(userAvatarDomainService.isAvailableBody(avatarBodyDto, activityMap))
                            .build()
                    ).toList();
        }
    }

    @Transactional
    public void updateAvatarBodyUri(UpdateAvatarBodyCommand avatarBodyCommand) {
        UserCredentials userCredentials = UserContext.getUserCredentials();
        Member member = memberRepository.findByUserId(userCredentials.getUserId()).orElseThrow().toDomain();
        Member updatedMember = member.updateAvatarBody(new AvatarBodyUri(avatarBodyCommand.getAvatarBodyUri()));
        memberRepository.save(updatedMember.toData());
    }

    @Transactional
    public void updateAvatarFaceUri(UpdateAvatarFaceCommand avatarFaceCommand) {
        UserCredentials userCredentials = UserContext.getUserCredentials();
        Member member = memberRepository.findByUserId(userCredentials.getUserId()).orElseThrow().toDomain();
        Member updatedMember = member.updateAvatarFace(new AvatarFaceUri(avatarFaceCommand.getAvatarFaceUri()));
        memberRepository.save(updatedMember.toData());
    }
}