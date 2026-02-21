package com.pfplaybackend.api.admin.adapter.out.external;

import com.pfplaybackend.api.admin.application.port.out.AdminAvatarResourcePort;
import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.user.application.dto.shared.AvatarIconDto;
import com.pfplaybackend.api.user.application.service.AvatarResourceQueryService;
import com.pfplaybackend.api.user.application.service.UserAvatarCommandService;
import com.pfplaybackend.api.user.domain.entity.data.AvatarBodyResourceData;
import com.pfplaybackend.api.user.domain.entity.data.AvatarFaceResourceData;
import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import com.pfplaybackend.api.user.domain.value.AvatarIconUri;
import com.pfplaybackend.api.user.adapter.out.persistence.AvatarBodyResourceRepository;
import com.pfplaybackend.api.user.adapter.out.persistence.AvatarFaceResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAvatarResourceAdapter implements AdminAvatarResourcePort {

    private final AvatarBodyResourceRepository avatarBodyResourceRepository;
    private final AvatarFaceResourceRepository avatarFaceResourceRepository;
    private final AvatarResourceQueryService avatarResourceQueryService;
    private final UserAvatarCommandService userAvatarCommandService;

    @Override
    public List<AvatarBodyResourceData> findAllAvatarBodyResources() {
        return avatarBodyResourceRepository.findAll();
    }

    @Override
    public List<AvatarFaceResourceData> findAllAvatarFaceResources() {
        return avatarFaceResourceRepository.findAll();
    }

    @Override
    public AvatarBodyDto findAvatarBodyByUri(AvatarBodyUri uri) {
        return avatarResourceQueryService.findAvatarBodyByUri(uri);
    }

    @Override
    public AvatarIconUri findAvatarIconPairWithSingleBody(AvatarBodyDto bodyDto) {
        return userAvatarCommandService.findAvatarIconPairWithSingleBody(bodyDto);
    }

    @Override
    public AvatarIconDto findPairAvatarIconByFaceUri(AvatarFaceUri uri) {
        return avatarResourceQueryService.findPairAvatarIconByFaceUri(uri);
    }
}
