package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import com.pfplaybackend.api.user.application.aspect.context.UserContext;
import com.pfplaybackend.api.user.application.dto.command.UpdateAvatarBodyCommand;
import com.pfplaybackend.api.user.application.dto.command.UpdateAvatarFaceCommand;
import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.user.application.dto.shared.AvatarFaceDto;
import com.pfplaybackend.api.user.application.dto.shared.AvatarIconDto;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Activity;
import com.pfplaybackend.api.user.domain.entity.domainmodel.AvatarBodyResource;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Member;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import com.pfplaybackend.api.user.domain.service.UserAvatarDomainService;
import com.pfplaybackend.api.user.domain.service.UserDomainService;
import com.pfplaybackend.api.user.domain.value.AvatarIconUri;
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
    // Using Peer Service
    private final AvatarResourceService avatarResourceService;

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
        UserContext userContext = (UserContext) ThreadLocalContext.getContext();
        List<AvatarBodyDto> avatarBodyDtoList = avatarResourceService.findAllAvatarBodies();
        if(userDomainService.isGuest(userContext)) {
            return avatarBodyDtoList;
        }else {
            Member member = memberRepository.findByUserId(userContext.getUserId()).orElseThrow().toDomain();
            Map<ActivityType, Activity> activityMap = member.getActivityMap();
            return avatarBodyDtoList.stream()
                    .map(avatarBodyDto -> avatarBodyDto.toBuilder()
                            .isAvailable(userAvatarDomainService.isAvailableBody(avatarBodyDto, activityMap))
                            .build()
                    ).toList();
        }
    }

    @Transactional
    public void updateAvatarBodyUri(UpdateAvatarBodyCommand command) {
        UserContext userContext = (UserContext) ThreadLocalContext.getContext();
        Member member = memberRepository.findByUserId(userContext.getUserId()).orElseThrow().toDomain();
        AvatarBodyDto avatarBodyDto = avatarResourceService.findAvatarBodyByUri(command.getAvatarBodyUri());
        AvatarFaceUri avatarFaceUri = userAvatarDomainService.updateFaceUriOnBodyUriChange(member, avatarBodyDto);
        AvatarIconUri avatarIconUri = userAvatarDomainService.updateIconUriOnBodyUriChange(member, avatarBodyDto);
        // TODO Check if the score is actually configurable in Domain Service
        Member updatedMember = member.updateAvatarBody(avatarBodyDto)
                .updateAvatarFace(avatarFaceUri)
                .updateAvatarIcon(avatarIconUri);
        memberRepository.save(updatedMember.toData());
    }

    @Transactional
    public void updateAvatarFaceUri(UpdateAvatarFaceCommand avatarFaceCommand) {
        UserContext userContext = (UserContext) ThreadLocalContext.getContext();
        Member member = memberRepository.findByUserId(userContext.getUserId()).orElseThrow().toDomain();
        AvatarFaceUri avatarFaceUri = new AvatarFaceUri(avatarFaceCommand.getAvatarFaceUri());
        AvatarIconUri avatarIconUri = userAvatarDomainService.updateIconUriOnFaceUriChange(member, avatarFaceUri);
        Member updatedMember = member.updateAvatarFace(avatarFaceUri)
                .updateAvatarIcon(avatarIconUri);
        memberRepository.save(updatedMember.toData());
    }
}