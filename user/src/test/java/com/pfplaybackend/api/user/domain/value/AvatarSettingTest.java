package com.pfplaybackend.api.user.domain.value;

import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.user.domain.enums.FaceSourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AvatarSettingTest {

    private static final String BODY_PNG = "body.png";
    private static final String FACE_PNG = "face.png";
    private static final String ICON_PNG = "icon.png";

    private AvatarSetting avatarSetting;

    @BeforeEach
    void setUp() {
        avatarSetting = new AvatarSetting(
                new AvatarBodyUri(BODY_PNG),
                new AvatarFaceUri(FACE_PNG),
                new AvatarIconUri(ICON_PNG),
                AvatarCompositionType.BODY_WITH_FACE,
                FaceSourceType.INTERNAL_IMAGE,
                100, 200,
                0.5, 0.6, 1.0
        );
    }

    @Test
    @DisplayName("생성 시 모든 필드가 올바르게 설정됨")
    void createAllFieldsSet() {
        assertThat(avatarSetting.getAvatarBodyUri().getValue()).isEqualTo(BODY_PNG);
        assertThat(avatarSetting.getAvatarFaceUri().getValue()).isEqualTo(FACE_PNG);
        assertThat(avatarSetting.getAvatarIconUri().getValue()).isEqualTo(ICON_PNG);
        assertThat(avatarSetting.getAvatarCompositionType()).isEqualTo(AvatarCompositionType.BODY_WITH_FACE);
        assertThat(avatarSetting.getFaceSourceType()).isEqualTo(FaceSourceType.INTERNAL_IMAGE);
        assertThat(avatarSetting.getCombinePositionX()).isEqualTo(100);
        assertThat(avatarSetting.getCombinePositionY()).isEqualTo(200);
        assertThat(avatarSetting.getOffsetX()).isEqualTo(0.5);
        assertThat(avatarSetting.getOffsetY()).isEqualTo(0.6);
        assertThat(avatarSetting.getScale()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("updateBody 호출 시 body URI와 position 변경")
    void updateBodyChangesBodyAndPosition() {
        // when
        avatarSetting.updateBody(new AvatarBodyUri("new-body.png"), 300, 400);

        // then
        assertThat(avatarSetting.getAvatarBodyUri().getValue()).isEqualTo("new-body.png");
        assertThat(avatarSetting.getCombinePositionX()).isEqualTo(300);
        assertThat(avatarSetting.getCombinePositionY()).isEqualTo(400);
        // face, icon은 변경되지 않음
        assertThat(avatarSetting.getAvatarFaceUri().getValue()).isEqualTo(FACE_PNG);
    }

    @Test
    @DisplayName("updateFaceSingleBody 호출 시 compositionType이 SINGLE_BODY로 변경")
    void updateFaceSingleBodyChangesCompositionType() {
        // when
        avatarSetting.updateFaceSingleBody(new AvatarFaceUri(""));

        // then
        assertThat(avatarSetting.getAvatarCompositionType()).isEqualTo(AvatarCompositionType.SINGLE_BODY);
        assertThat(avatarSetting.getAvatarFaceUri().getValue()).isEmpty();
    }

    @Test
    @DisplayName("updateFaceWithTransform 호출 시 compositionType과 transform 값 변경")
    void updateFaceWithTransformChangesAllTransformFields() {
        // given
        avatarSetting.updateFaceSingleBody(new AvatarFaceUri(""));
        assertThat(avatarSetting.getAvatarCompositionType()).isEqualTo(AvatarCompositionType.SINGLE_BODY);

        // when
        avatarSetting.updateFaceWithTransform(
                new AvatarFaceUri("custom-face.png"),
                FaceSourceType.NFT_URI,
                1.5, 2.5, 3.0
        );

        // then
        assertThat(avatarSetting.getAvatarCompositionType()).isEqualTo(AvatarCompositionType.BODY_WITH_FACE);
        assertThat(avatarSetting.getFaceSourceType()).isEqualTo(FaceSourceType.NFT_URI);
        assertThat(avatarSetting.getAvatarFaceUri().getValue()).isEqualTo("custom-face.png");
        assertThat(avatarSetting.getOffsetX()).isEqualTo(1.5);
        assertThat(avatarSetting.getOffsetY()).isEqualTo(2.5);
        assertThat(avatarSetting.getScale()).isEqualTo(3.0);
    }

    @Test
    @DisplayName("updateIcon 호출 시 icon URI만 변경")
    void updateIconChangesOnlyIcon() {
        // when
        avatarSetting.updateIcon(new AvatarIconUri("new-icon.png"));

        // then
        assertThat(avatarSetting.getAvatarIconUri().getValue()).isEqualTo("new-icon.png");
        assertThat(avatarSetting.getAvatarBodyUri().getValue()).isEqualTo(BODY_PNG);
    }

    @Test
    @DisplayName("applyDefaults는 null인 URI만 빈 문자열로 초기화")
    void applyDefaultsOnlyFillsNullUris() {
        // given
        AvatarSetting empty = new AvatarSetting(
                null, null, null,
                AvatarCompositionType.SINGLE_BODY, null,
                0, 0, 0, 0, 0
        );

        // when
        empty.applyDefaults();

        // then
        assertThat(empty.getAvatarBodyUri().getValue()).isEmpty();
        assertThat(empty.getAvatarFaceUri().getValue()).isEmpty();
        assertThat(empty.getAvatarIconUri().getValue()).isEmpty();
    }

    @Test
    @DisplayName("applyDefaults는 이미 설정된 URI를 덮어쓰지 않음")
    void applyDefaultsDoesNotOverwriteExistingValues() {
        // when
        avatarSetting.applyDefaults();

        // then
        assertThat(avatarSetting.getAvatarBodyUri().getValue()).isEqualTo(BODY_PNG);
        assertThat(avatarSetting.getAvatarFaceUri().getValue()).isEqualTo(FACE_PNG);
        assertThat(avatarSetting.getAvatarIconUri().getValue()).isEqualTo(ICON_PNG);
    }
}
