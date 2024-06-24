package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.user.application.dto.shared.AvatarFaceDto;
import com.pfplaybackend.api.user.domain.entity.domainmodel.AvatarResource;
import com.pfplaybackend.api.user.repository.AvatarResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvatarResourceService {

    private final AvatarResourceRepository avatarResourceRepository;

    public AvatarResource getDefaultSettingResource() {
        return avatarResourceRepository.getDefaultSettingResource().orElseThrow().toDomain();
    }

    public AvatarFaceDto getDefaultSettingFace() {
        return AvatarFaceDto.builder()
                .name("기본 Face")
                .resourceUri("https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/avatar_body_1.png?alt=media")
                .build();
    }

    public List<AvatarBodyDto> findAllAvatarBodies() {
        return avatarResourceRepository.findAllAvatarResources().orElseThrow()
                .stream()
                .map(avatarResourceData -> AvatarBodyDto.create(avatarResourceData.toDomain())
                ).toList();
    }
}
