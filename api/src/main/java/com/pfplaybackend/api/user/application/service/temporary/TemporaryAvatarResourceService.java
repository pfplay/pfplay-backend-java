package com.pfplaybackend.api.user.application.service.temporary;

import com.pfplaybackend.api.user.domain.entity.data.AvatarResourceData;
import com.pfplaybackend.api.user.domain.entity.domainmodel.AvatarResource;
import com.pfplaybackend.api.user.domain.enums.ObtainmentType;
import com.pfplaybackend.api.user.repository.AvatarResourceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemporaryAvatarResourceService {
    private final AvatarResourceRepository avatarResourceRepository;

    @Transactional
    public void addTemporaryAvatarBodies() {
        // TODO 기본 아바타 Body 리소스 추가
        addAvatarBody("기본 제공 샘플-1[디폴트]", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/avatar_body_1.png?alt=media", ObtainmentType.BASIC, 0, false, true);
        addAvatarBody("기본 제공 샘플-2", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/avatar_body_1.png?alt=media", ObtainmentType.BASIC, 0, true, false);
        addAvatarBody("디제잉 포인트 해금 샘플", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/avatar_body_1.png?alt=media", ObtainmentType.DJ_PNT, 10, true, false);
//        addAvatarBody("레퍼럴 링크 해금 샘플", "URI-4", ObtainmentType.REF_LINK, 20, true, false);
//        addAvatarBody("파티룸 활성화 해금 샘플", "URI-5", ObtainmentType.ROOM_ACT, 30, true, false);
    }

    private void addAvatarBody(String name, String resourceUri, ObtainmentType obtainableType,
                               int obtainableScore, boolean isCombinable, boolean isDefaultSetting) {
        AvatarResource avatarResource = AvatarResource.create(name, resourceUri, obtainableType, obtainableScore, isCombinable, isDefaultSetting);
        AvatarResourceData avatarResourceData = avatarResource.toData();
        avatarResourceRepository.save(avatarResourceData);
    }
}
