package com.pfplaybackend.api.profile.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BioTest {

    @Test
    @DisplayName("닉네임과 소개글로 Bio 생성")
    void create_withNicknameAndIntroduction() {
        // given
        Nickname nickname = new Nickname("Player1");

        // when
        Bio bio = new Bio(nickname, "Hello world");

        // then
        assertThat(bio.getNicknameValue()).isEqualTo("Player1");
        assertThat(bio.getIntroduction()).isEqualTo("Hello world");
    }

    @Test
    @DisplayName("nickname이 null이면 getNicknameValue는 null 반환")
    void getNicknameValue_whenNicknameIsNull_returnsNull() {
        // given
        Bio bio = new Bio(null, "intro");

        // when / then
        assertThat(bio.getNicknameValue()).isNull();
    }

    @Test
    @DisplayName("introduction이 null이어도 Bio 생성 가능")
    void create_withNullIntroduction() {
        // given / when
        Bio bio = new Bio(new Nickname("Player1"), null);

        // then
        assertThat(bio.getNicknameValue()).isEqualTo("Player1");
        assertThat(bio.getIntroduction()).isNull();
    }

    @Test
    @DisplayName("update 호출 시 닉네임과 소개글 변경")
    void update_changesNicknameAndIntroduction() {
        // given
        Bio bio = new Bio(new Nickname("OldName"), "old intro");

        // when
        bio.update("NewName", "new intro");

        // then
        assertThat(bio.getNicknameValue()).isEqualTo("NewName");
        assertThat(bio.getIntroduction()).isEqualTo("new intro");
    }

    @Test
    @DisplayName("update 시 유효하지 않은 닉네임은 예외 발생")
    void update_withInvalidNickname_throwsException() {
        // given
        Bio bio = new Bio(new Nickname("ValidName"), "intro");

        // when / then
        assertThatThrownBy(() -> bio.update("", "new intro"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
