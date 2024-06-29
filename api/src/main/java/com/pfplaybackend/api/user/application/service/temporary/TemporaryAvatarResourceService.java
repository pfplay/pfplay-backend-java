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
        // 기본 아바타 Body 리소스 추가
        addAvatarBody("ava_basic_001", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_basic%2Fava_basic_001.png?alt=media", ObtainmentType.BASIC, 0, true, true, 60, 9);
        addAvatarBody("ava_basic_002", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_basic%2Fava_basic_002.png?alt=media", ObtainmentType.BASIC, 0, false, false, 0, 0);
        addAvatarBody("ava_basic_003", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_basic%2Fava_basic_003.png?alt=media", ObtainmentType.BASIC, 0, false, false, 0, 0);
        // 디제잉 해금 아바타 Body 리소스 추가
        addAvatarBody("ava_djing_001", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_001.png?alt=media", ObtainmentType.DJ_PNT, 10, false, false, 0, 0);
        addAvatarBody("ava_djing_002", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_002.png?alt=media", ObtainmentType.DJ_PNT, 25, false, false, 0, 0);
        addAvatarBody("ava_djing_003", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_003.png?alt=media", ObtainmentType.DJ_PNT, 60, true, false, 59, 24);
        addAvatarBody("ava_djing_004", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_004.png?alt=media", ObtainmentType.DJ_PNT, 100, false, false, 60, 11);
        addAvatarBody("ava_djing_005", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_005.png?alt=media", ObtainmentType.DJ_PNT, 150, true, false, 60, 35);
        addAvatarBody("ava_djing_006", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_006.png?alt=media", ObtainmentType.DJ_PNT, 200, true, false, 53, 37);
        addAvatarBody("ava_djing_007", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_007.png?alt=media", ObtainmentType.DJ_PNT, 500, false, false, 60, 12);
        addAvatarBody("ava_djing_008", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_008.png?alt=media", ObtainmentType.DJ_PNT, 1000, true, false, 60, 26);
        // addAvatarBody("ava_djing_009", ""https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_009.png?alt=media"", ObtainmentType.DJ_PNT, 10, true, false);
        addAvatarBody("ava_djing_010", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_010.png?alt=media", ObtainmentType.DJ_PNT, 4000, true, false, 58, 23);
        addAvatarBody("ava_djing_011", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_011.png?alt=media", ObtainmentType.DJ_PNT, 7000, true, false, 60, 34);
        addAvatarBody("ava_djing_012", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_012.png?alt=media", ObtainmentType.DJ_PNT, 10000, true, false, 57, 11);
    }

    private void addAvatarBody(String name, String resourceUri, ObtainmentType obtainableType,
                               int obtainableScore, boolean isCombinable, boolean isDefaultSetting, int x, int y) {
        AvatarResource avatarResource = AvatarResource.create(name, resourceUri, obtainableType, obtainableScore,
                isCombinable, isDefaultSetting, x , y);
        AvatarResourceData avatarResourceData = avatarResource.toData();
        avatarResourceRepository.save(avatarResourceData);
    }
}
