package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.user.application.dto.shared.AvatarFaceDto;
import com.pfplaybackend.api.user.application.dto.shared.AvatarIconDto;
import com.pfplaybackend.api.user.domain.entity.data.AvatarBodyResourceData;
import com.pfplaybackend.api.user.domain.entity.data.AvatarFaceResourceData;
import com.pfplaybackend.api.user.domain.entity.data.AvatarIconResourceData;
import com.pfplaybackend.api.user.domain.entity.domainmodel.AvatarBodyResource;
import com.pfplaybackend.api.user.domain.entity.domainmodel.AvatarFaceResource;
import com.pfplaybackend.api.user.domain.entity.domainmodel.AvatarIconResource;
import com.pfplaybackend.api.user.domain.enums.PairType;
import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import com.pfplaybackend.api.user.domain.value.AvatarIconUri;
import com.pfplaybackend.api.user.repository.AvatarBodyResourceRepository;
import com.pfplaybackend.api.user.repository.AvatarFaceResourceRepository;
import com.pfplaybackend.api.user.repository.AvatarIconResourceRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AvatarResourceService {

    private final AvatarBodyResourceRepository avatarBodyResourceRepository;
    private final AvatarFaceResourceRepository avatarFaceResourceRepository;
    private final AvatarIconResourceRepository avatarIconResourceRepository;

    public AvatarBodyResource getDefaultSettingResourceAvatarBody() {
        return avatarBodyResourceRepository.getDefaultSettingResource().orElseThrow().toDomain();
    }

    public List<AvatarFaceDto> findAllAvatarFaces() {
        return avatarFaceResourceRepository.findAll().stream().map(avatarResourceData ->
                AvatarFaceDto.builder()
                        .id(avatarResourceData.getId())
                        .name(avatarResourceData.getName())
                        .resourceUri(avatarResourceData.getResourceUri())
                        .isAvailable(true)
                        .build()).toList();
    }

    public List<AvatarBodyDto> findAllAvatarBodies() {
        return avatarBodyResourceRepository.findAllAvatarResources().orElseThrow()
                .stream()
                .map(avatarResourceData -> AvatarBodyDto.create(avatarResourceData.toDomain())
                ).toList();
    }

    public AvatarBodyDto findAvatarBodyByUri(AvatarBodyUri uri) {
        AvatarBodyResourceData avatarBodyResourceData = avatarBodyResourceRepository.findOneAvatarResourceByResourceUri(uri.getAvatarBodyUri());
        return AvatarBodyDto.create(avatarBodyResourceData.toDomain());
    }

    public AvatarIconDto findPairAvatarIconByFaceUri(AvatarFaceUri uri) {
        AvatarFaceResourceData avatarFaceResourceData = avatarFaceResourceRepository.findOneAvatarResourceByResourceUri(uri.getAvatarFaceUri());
        String avatarFaceName = avatarFaceResourceData.getName();
        String iconName = "ava_icon_" + avatarFaceName.split("_", 2)[1];
        AvatarIconResourceData avatarIconResourceData = avatarIconResourceRepository.findByNameAndPairType(iconName, PairType.FACE);
        return AvatarIconDto.builder()
                .id(avatarIconResourceData.getId())
                .name(avatarIconResourceData.getName())
                .resourceUri(avatarIconResourceData.getResourceUri())
                .build();
    }

    public AvatarIconDto findPairAvatarIconByBodyUri(AvatarBodyUri uri) {
        AvatarBodyResourceData avatarBodyResourceData = avatarBodyResourceRepository.findOneAvatarResourceByResourceUri(uri.getAvatarBodyUri());
        String avatarBodyName = avatarBodyResourceData.getName();
        String iconName = "ava_icon_" + avatarBodyName.split("_", 2)[1];
        AvatarIconResourceData avatarIconResourceData = avatarIconResourceRepository.findByNameAndPairType(iconName, PairType.BODY);
        return AvatarIconDto.builder()
                .id(avatarIconResourceData.getId())
                .name(avatarIconResourceData.getName())
                .resourceUri(avatarIconResourceData.getResourceUri())
                .build();
    }

    public boolean isBasicFaceUri(AvatarFaceUri avatarFaceUri) {
        return avatarFaceResourceRepository.findByResourceUri(avatarFaceUri.getAvatarFaceUri()).isPresent();
    }
}