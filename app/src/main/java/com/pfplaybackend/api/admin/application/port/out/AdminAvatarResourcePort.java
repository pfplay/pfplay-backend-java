package com.pfplaybackend.api.admin.application.port.out;

import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.user.application.dto.shared.AvatarIconDto;
import com.pfplaybackend.api.user.domain.entity.data.AvatarBodyResourceData;
import com.pfplaybackend.api.user.domain.entity.data.AvatarFaceResourceData;
import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import com.pfplaybackend.api.user.domain.value.AvatarIconUri;

import java.util.List;

public interface AdminAvatarResourcePort {
    List<AvatarBodyResourceData> findAllAvatarBodyResources();
    List<AvatarFaceResourceData> findAllAvatarFaceResources();
    AvatarBodyDto findAvatarBodyByUri(AvatarBodyUri uri);
    AvatarIconUri findAvatarIconPairWithSingleBody(AvatarBodyDto bodyDto);
    AvatarIconDto findPairAvatarIconByFaceUri(AvatarFaceUri uri);
}
