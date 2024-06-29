package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.user.application.dto.shared.AvatarFaceDto;
import com.pfplaybackend.api.user.domain.entity.domainmodel.AvatarResource;
import com.pfplaybackend.api.user.repository.AvatarResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvatarResourceService {

    private final AvatarResourceRepository avatarResourceRepository;

    public AvatarResource getDefaultSettingResourceAvatarBody() {
        return avatarResourceRepository.getDefaultSettingResource().orElseThrow().toDomain();
    }

    public List<AvatarFaceDto> findAllAvatarFaces() {
        return new ArrayList<>(Collections.singletonList(AvatarFaceDto.builder()
                .id(1)
                .name("ava_face_001")
                .resourceUri("https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_face%2Fava_face_001.png?alt=media")
                .isAvailable(true)
                .build())
        );
    }

    public List<AvatarBodyDto> findAllAvatarBodies() {
        return avatarResourceRepository.findAllAvatarResources().orElseThrow()
                .stream()
                .map(avatarResourceData -> AvatarBodyDto.create(avatarResourceData.toDomain())
                ).toList();
    }
}
