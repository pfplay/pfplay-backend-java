package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.user.domain.model.domain.AvatarResource;
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

    public List<AvatarBodyDto>  findAllAvatarBodies() {
        return avatarResourceRepository.findAllAvatarResources().orElseThrow()
                .stream()
                .map(avatarResourceData -> AvatarBodyDto.create(avatarResourceData.toDomain())
                ).toList();
    }
}
