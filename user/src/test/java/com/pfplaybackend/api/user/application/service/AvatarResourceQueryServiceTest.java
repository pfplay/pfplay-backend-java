package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.user.application.dto.shared.AvatarIconDto;
import com.pfplaybackend.api.user.domain.entity.data.AvatarBodyResourceData;
import com.pfplaybackend.api.user.domain.entity.data.AvatarFaceResourceData;
import com.pfplaybackend.api.user.domain.entity.data.AvatarIconResourceData;
import com.pfplaybackend.api.user.domain.enums.ObtainmentType;
import com.pfplaybackend.api.user.domain.enums.PairType;
import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import com.pfplaybackend.api.user.adapter.out.persistence.AvatarBodyResourceRepository;
import com.pfplaybackend.api.user.adapter.out.persistence.AvatarFaceResourceRepository;
import com.pfplaybackend.api.user.adapter.out.persistence.AvatarIconResourceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvatarResourceQueryServiceTest {

    @Mock AvatarBodyResourceRepository avatarBodyResourceRepository;
    @Mock AvatarFaceResourceRepository avatarFaceResourceRepository;
    @Mock AvatarIconResourceRepository avatarIconResourceRepository;
    @InjectMocks AvatarResourceQueryService avatarResourceQueryService;

    @Test
    @DisplayName("findPairAvatarIconByFaceUri — 이름 기반으로 아이콘 URI를 생성한다")
    void findPairAvatarIconByFaceUriGeneratesIconName() {
        // given
        AvatarFaceUri faceUri = new AvatarFaceUri("face_uri_01");
        AvatarFaceResourceData faceData = AvatarFaceResourceData.builder()
                .id(1L).name("ava_face_happy").resourceUri("face_uri_01").build();
        when(avatarFaceResourceRepository.findOneAvatarResourceByResourceUri("face_uri_01"))
                .thenReturn(faceData);

        AvatarIconResourceData iconData = AvatarIconResourceData.builder()
                .id(1L).name("ava_icon_face_happy").resourceUri("icon_uri_01").pairType(PairType.FACE).build();
        when(avatarIconResourceRepository.findByNameAndPairType("ava_icon_face_happy", PairType.FACE))
                .thenReturn(iconData);

        // when
        AvatarIconDto result = avatarResourceQueryService.findPairAvatarIconByFaceUri(faceUri);

        // then
        assertThat(result.resourceUri()).isEqualTo("icon_uri_01");
        assertThat(result.name()).isEqualTo("ava_icon_face_happy");
    }

    @Test
    @DisplayName("findPairAvatarIconByBodyUri — 바디 URI 기반으로 아이콘 URI를 생성한다")
    void findPairAvatarIconByBodyUriGeneratesIconName() {
        // given
        AvatarBodyUri bodyUri = new AvatarBodyUri("body_uri_01");
        AvatarBodyResourceData bodyData = AvatarBodyResourceData.builder()
                .id(1L).name("ava_body_cool").resourceUri("body_uri_01")
                .obtainableType(ObtainmentType.BASIC).obtainableScore(0)
                .isCombinable(true).isDefaultSetting(true)
                .combinePositionX(0).combinePositionY(0)
                .build();
        when(avatarBodyResourceRepository.findOneAvatarResourceByResourceUri("body_uri_01"))
                .thenReturn(bodyData);

        AvatarIconResourceData iconData = AvatarIconResourceData.builder()
                .id(2L).name("ava_icon_body_cool").resourceUri("icon_uri_02").pairType(PairType.BODY).build();
        when(avatarIconResourceRepository.findByNameAndPairType("ava_icon_body_cool", PairType.BODY))
                .thenReturn(iconData);

        // when
        AvatarIconDto result = avatarResourceQueryService.findPairAvatarIconByBodyUri(bodyUri);

        // then
        assertThat(result.resourceUri()).isEqualTo("icon_uri_02");
        assertThat(result.name()).isEqualTo("ava_icon_body_cool");
    }

    @Test
    @DisplayName("isBasicFaceUri — 기본 얼굴 URI를 올바르게 판별한다")
    void isBasicFaceUriReturnsCorrectly() {
        // given
        AvatarFaceUri basicFace = new AvatarFaceUri("basic_face_uri");
        AvatarFaceUri customFace = new AvatarFaceUri("custom_nft_uri");

        when(avatarFaceResourceRepository.findByResourceUri("basic_face_uri"))
                .thenReturn(Optional.of(AvatarFaceResourceData.builder()
                        .id(1L).name("basic").resourceUri("basic_face_uri").build()));
        when(avatarFaceResourceRepository.findByResourceUri("custom_nft_uri"))
                .thenReturn(Optional.empty());

        // when & then
        assertThat(avatarResourceQueryService.isBasicFaceUri(basicFace)).isTrue();
        assertThat(avatarResourceQueryService.isBasicFaceUri(customFace)).isFalse();
    }

    @Test
    @DisplayName("getDefaultSettingResourceAvatarBody — 기본 바디를 반환한다")
    void getDefaultSettingResourceAvatarBodyReturnsDefault() {
        // given
        AvatarBodyResourceData defaultBody = AvatarBodyResourceData.builder()
                .id(1L).name("default").resourceUri("default-body-uri")
                .obtainableType(ObtainmentType.BASIC).obtainableScore(0)
                .isCombinable(true).isDefaultSetting(true)
                .combinePositionX(0).combinePositionY(0)
                .build();
        when(avatarBodyResourceRepository.getDefaultSettingResource()).thenReturn(Optional.of(defaultBody));

        // when
        AvatarBodyResourceData result = avatarResourceQueryService.getDefaultSettingResourceAvatarBody();

        // then
        assertThat(result.getName()).isEqualTo("default");
        assertThat(result.isDefaultSetting()).isTrue();
    }
}
