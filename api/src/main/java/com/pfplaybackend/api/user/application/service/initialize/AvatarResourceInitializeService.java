package com.pfplaybackend.api.user.application.service.initialize;

import com.pfplaybackend.api.avatarresource.adapter.out.persistence.AvatarBodyResourceRepository;
import com.pfplaybackend.api.avatarresource.adapter.out.persistence.AvatarFaceResourceRepository;
import com.pfplaybackend.api.avatarresource.adapter.out.persistence.AvatarIconResourceRepository;
import com.pfplaybackend.api.user.domain.entity.data.AvatarBodyResourceData;
import com.pfplaybackend.api.user.domain.entity.data.AvatarFaceResourceData;
import com.pfplaybackend.api.user.domain.entity.data.AvatarIconResourceData;
import com.pfplaybackend.api.user.domain.enums.ObtainmentType;
import com.pfplaybackend.api.user.domain.enums.PairType;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AvatarResourceInitializeService {
    private final AvatarBodyResourceRepository avatarBodyResourceRepository;
    private final AvatarFaceResourceRepository avatarFaceResourceRepository;
    private final AvatarIconResourceRepository avatarIconResourceRepository;

    @Transactional
    public void addAvatarBodies() {
        // 기본 아바타 Body 리소스 추가
        addAvatarBody("ava_body_basic_001", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_basic%2Fava_basic_001.png?alt=media", ObtainmentType.BASIC, 0, true, true, 60, 41);
        addAvatarBody("ava_body_basic_002", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_basic%2Fava_basic_002.png?alt=media", ObtainmentType.BASIC, 0, false, false, 0, 0);
        addAvatarBody("ava_body_basic_003", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_basic%2Fava_basic_003.png?alt=media", ObtainmentType.BASIC, 0, false, false, 0, 0);
        // 디제잉 해금 아바타 Body 리소스 추가
        addAvatarBody("ava_body_djing_001", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_001.png?alt=media", ObtainmentType.DJ_PNT, 10, false, false, 0, 0);
        addAvatarBody("ava_body_djing_002", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_002.png?alt=media", ObtainmentType.DJ_PNT, 25, false, false, 0, 0);
        addAvatarBody("ava_body_djing_003", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_003.png?alt=media", ObtainmentType.DJ_PNT, 60, true, false, 60, 39);
        addAvatarBody("ava_body_djing_004", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_004.png?alt=media", ObtainmentType.DJ_PNT, 100, true, false, 60, 45);
        addAvatarBody("ava_body_djing_005", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_005.png?alt=media", ObtainmentType.DJ_PNT, 150, true, false, 60, 40);
        addAvatarBody("ava_body_djing_006", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_006.png?alt=media", ObtainmentType.DJ_PNT, 200, true, false, 52, 39);
        addAvatarBody("ava_body_djing_007", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_007.png?alt=media", ObtainmentType.DJ_PNT, 500, true, false, 60, 40);
        addAvatarBody("ava_body_djing_008", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_008.png?alt=media", ObtainmentType.DJ_PNT, 1000, true, false, 60, 40);
        addAvatarBody("ava_body_djing_009", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_009.png?alt=media", ObtainmentType.DJ_PNT, 2000, true, false, 60, 42);
        addAvatarBody("ava_body_djing_010", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_010.png?alt=media", ObtainmentType.DJ_PNT, 4000, true, false, 58, 43);
        addAvatarBody("ava_body_djing_011", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_011.png?alt=media", ObtainmentType.DJ_PNT, 7000, true, false, 60, 44);
        addAvatarBody("ava_body_djing_012", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_djing%2Fava_djing_012.png?alt=media", ObtainmentType.DJ_PNT, 10000, true, false, 56, 38);
    }

    @Transactional
    public void addAvatarFaces() {
        addAvatarFace("ava_face_basic_001", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_face%2Fava_face_001.png?alt=media");
    }

    @Transactional
    public void addAvatarIcons() {
        addAvatarIcon("ava_icon_face_basic_001", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_icon%2Fava_icon_face_basic_001.png?alt=media", PairType.FACE);
        addAvatarIcon("ava_icon_body_basic_002", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_icon%2Fava_icon_body_basic_002.png?alt=media", PairType.BODY);
        addAvatarIcon("ava_icon_body_basic_003", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_icon%2Fava_icon_body_basic_003.png?alt=media", PairType.BODY);
        addAvatarIcon("ava_icon_body_djing_001", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_icon%2Fava_icon_body_djing_001.png?alt=media", PairType.BODY);
        addAvatarIcon("ava_icon_body_djing_002", "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/ava_icon%2Fava_icon_body_djing_002.png?alt=media", PairType.BODY);
    }

    private void addAvatarBody(String name, String resourceUri, ObtainmentType obtainableType,
                               int obtainableScore, boolean isCombinable, boolean isDefaultSetting, int x, int y) {
        Optional<AvatarBodyResourceData> existing = avatarBodyResourceRepository.findByName(name);
        if (existing.isPresent()) {
            AvatarBodyResourceData data = existing.get();
            data.updateResource(resourceUri, obtainableType, obtainableScore, isCombinable, isDefaultSetting, x, y);
            avatarBodyResourceRepository.save(data);
        } else {
            AvatarBodyResourceData avatarBodyResourceData = AvatarBodyResourceData.create(name, resourceUri, obtainableType, obtainableScore,
                    isCombinable, isDefaultSetting, x, y);
            avatarBodyResourceRepository.save(avatarBodyResourceData);
        }
    }

    private void addAvatarFace(String name, String resourceUri) {
        AvatarFaceResourceData avatarFaceResourceData = AvatarFaceResourceData.create(name, resourceUri);
        avatarFaceResourceRepository.save(avatarFaceResourceData);
    }

    private void addAvatarIcon(String name, String resourceUri, PairType pairType) {
        AvatarIconResourceData avatarIconResourceData = AvatarIconResourceData.create(name, resourceUri, pairType);
        avatarIconResourceRepository.save(avatarIconResourceData);
    }
}
