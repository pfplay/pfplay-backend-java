package com.pfplaybackend.api.user.application.service.initialize;

import com.pfplaybackend.api.user.domain.entity.data.AvatarBodyResourceData;
import com.pfplaybackend.api.user.domain.entity.domainmodel.AvatarBodyResource;
import com.pfplaybackend.api.user.domain.entity.domainmodel.AvatarFaceResource;
import com.pfplaybackend.api.user.domain.entity.domainmodel.AvatarIconResource;
import com.pfplaybackend.api.user.domain.enums.ObtainmentType;
import com.pfplaybackend.api.user.repository.AvatarBodyResourceRepository;
import com.pfplaybackend.api.user.repository.AvatarFaceResourceRepository;
import com.pfplaybackend.api.user.repository.AvatarIconResourceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvatarResourceInitializeService {
    private final AvatarBodyResourceRepository avatarBodyResourceRepository;
    private final AvatarFaceResourceRepository avatarFaceResourceRepository;
    private final AvatarIconResourceRepository avatarIconResourceRepository;

    @Transactional
    public void addAvatarBodies() {
        // 기본 아바타 Body 리소스 추가
        addAvatarBody("ava_basic_001", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_basic%2Fava_basic_001.png?alt=media", ObtainmentType.BASIC, 0, true, true, 60, 9);
        addAvatarBody("ava_basic_002", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_basic%2Fava_basic_002.png?alt=media", ObtainmentType.BASIC, 0, false, false, 0, 0);
        addAvatarBody("ava_basic_003", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_basic%2Fava_basic_003.png?alt=media", ObtainmentType.BASIC, 0, false, false, 0, 0);
        // 디제잉 해금 아바타 Body 리소스 추가
        addAvatarBody("ava_djing_001", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_001.png?alt=media", ObtainmentType.DJ_PNT, 10, false, false, 0, 0);
        addAvatarBody("ava_djing_002", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_002.png?alt=media", ObtainmentType.DJ_PNT, 25, false, false, 0, 0);
        addAvatarBody("ava_djing_003", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_003.png?alt=media", ObtainmentType.DJ_PNT, 60, true, false, 59, 24);
        addAvatarBody("ava_djing_004", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_004.png?alt=media", ObtainmentType.DJ_PNT, 100, true, false, 60, 11);
        addAvatarBody("ava_djing_005", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_005.png?alt=media", ObtainmentType.DJ_PNT, 150, true, false, 60, 35);
        addAvatarBody("ava_djing_006", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_006.png?alt=media", ObtainmentType.DJ_PNT, 200, true, false, 53, 37);
        addAvatarBody("ava_djing_007", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_007.png?alt=media", ObtainmentType.DJ_PNT, 500, true, false, 60, 12);
        addAvatarBody("ava_djing_008", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_008.png?alt=media", ObtainmentType.DJ_PNT, 1000, true, false, 60, 26);
        addAvatarBody("ava_djing_009", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_009.png?alt=media", ObtainmentType.DJ_PNT, 2000, true, false, 100, 100);
        addAvatarBody("ava_djing_010", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_010.png?alt=media", ObtainmentType.DJ_PNT, 4000, true, false, 58, 23);
        addAvatarBody("ava_djing_011", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_011.png?alt=media", ObtainmentType.DJ_PNT, 7000, true, false, 60, 34);
        addAvatarBody("ava_djing_012", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_012.png?alt=media", ObtainmentType.DJ_PNT, 10000, true, false, 57, 11);
    }

    @Transactional
    public void addAvatarFaces() {
        addAvatarFace("ava_face_001", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_face%2Fava_face_001.png?alt=media");
    }

    @Transactional
    public void addAvatarIcons() {
        addAvatarIcon("ava_icon_guest", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_icon%2Fava_icon_guest.png?alt=media");
        addAvatarIcon("ava_icon_basic_002", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_icon%2Fava_icon_basic_002.png?alt=media");
        addAvatarIcon("ava_icon_basic_003", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_icon%2Fava_icon_basic_003.png?alt=media");
        addAvatarIcon("ava_icon_djing_001", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_icon%2Fava_icon_djing_001.png?alt=media");
        addAvatarIcon("ava_icon_djing_002", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_icon%2Fava_icon_djing_002.png?alt=media");
    }

    private void addAvatarBody(String name, String resourceUri, ObtainmentType obtainableType,
                               int obtainableScore, boolean isCombinable, boolean isDefaultSetting, int x, int y) {
        AvatarBodyResource avatarBodyResource = AvatarBodyResource.create(name, resourceUri, obtainableType, obtainableScore,
                isCombinable, isDefaultSetting, x , y);
        avatarBodyResourceRepository.save(avatarBodyResource.toData());
    }

    private void addAvatarFace(String name, String resourceUri) {
        AvatarFaceResource avatarFaceResource = AvatarFaceResource.create(name, resourceUri);
        avatarFaceResourceRepository.save(avatarFaceResource.toData());
    }

    private void addAvatarIcon(String name, String resourceUri) {
        AvatarIconResource avatarIconResource = AvatarIconResource.create(name, resourceUri);
        avatarIconResourceRepository.save(avatarIconResource.toData());
    }
}
