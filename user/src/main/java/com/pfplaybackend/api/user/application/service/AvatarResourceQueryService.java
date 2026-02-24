package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.user.adapter.out.persistence.AvatarBodyResourceRepository;
import com.pfplaybackend.api.user.adapter.out.persistence.AvatarFaceResourceRepository;
import com.pfplaybackend.api.user.adapter.out.persistence.AvatarIconResourceRepository;
import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.user.application.dto.shared.AvatarFaceDto;
import com.pfplaybackend.api.user.application.dto.shared.AvatarIconDto;
import com.pfplaybackend.api.user.domain.entity.data.AvatarBodyResourceData;
import com.pfplaybackend.api.user.domain.entity.data.AvatarFaceResourceData;
import com.pfplaybackend.api.user.domain.entity.data.AvatarIconResourceData;
import com.pfplaybackend.api.user.domain.enums.PairType;
import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvatarResourceQueryService {

    private final AvatarBodyResourceRepository avatarBodyResourceRepository;
    private final AvatarFaceResourceRepository avatarFaceResourceRepository;
    private final AvatarIconResourceRepository avatarIconResourceRepository;

    public AvatarBodyResourceData getDefaultSettingResourceAvatarBody() {
        return avatarBodyResourceRepository.getDefaultSettingResource().orElseThrow();
    }

    public List<AvatarFaceDto> findAllAvatarFaces() {
        return avatarFaceResourceRepository.findAll().stream().map(avatarResourceData ->
                new AvatarFaceDto(
                        avatarResourceData.getId(),
                        avatarResourceData.getName(),
                        avatarResourceData.getResourceUri(),
                        true
                )).toList();
    }

    public List<AvatarBodyDto> findAllAvatarBodies() {
        return avatarBodyResourceRepository.findAllAvatarResources().orElseThrow()
                .stream()
                .map(AvatarBodyDto::create
                ).toList();
    }

    public AvatarBodyDto findAvatarBodyByUri(AvatarBodyUri uri) {
        AvatarBodyResourceData avatarBodyResourceData = avatarBodyResourceRepository.findOneAvatarResourceByResourceUri(uri.getValue());
        return AvatarBodyDto.create(avatarBodyResourceData);
    }

    public AvatarIconDto findPairAvatarIconByFaceUri(AvatarFaceUri uri) {
        AvatarFaceResourceData avatarFaceResourceData = avatarFaceResourceRepository.findOneAvatarResourceByResourceUri(uri.getValue());
        String avatarFaceName = avatarFaceResourceData.getName();
        String iconName = "ava_icon_" + avatarFaceName.split("_", 2)[1];
        AvatarIconResourceData avatarIconResourceData = avatarIconResourceRepository.findByNameAndPairType(iconName, PairType.FACE);
        return new AvatarIconDto(
                avatarIconResourceData.getId(),
                avatarIconResourceData.getName(),
                avatarIconResourceData.getResourceUri(),
                true
        );
    }

    public AvatarIconDto findPairAvatarIconByBodyUri(AvatarBodyUri uri) {
        AvatarBodyResourceData avatarBodyResourceData = avatarBodyResourceRepository.findOneAvatarResourceByResourceUri(uri.getValue());
        String avatarBodyName = avatarBodyResourceData.getName();
        String iconName = "ava_icon_" + avatarBodyName.split("_", 2)[1];
        AvatarIconResourceData avatarIconResourceData = avatarIconResourceRepository.findByNameAndPairType(iconName, PairType.BODY);
        return new AvatarIconDto(
                avatarIconResourceData.getId(),
                avatarIconResourceData.getName(),
                avatarIconResourceData.getResourceUri(),
                true
        );
    }

    public boolean isBasicFaceUri(AvatarFaceUri avatarFaceUri) {
        return avatarFaceResourceRepository.findByResourceUri(avatarFaceUri.getValue()).isPresent();
    }
}