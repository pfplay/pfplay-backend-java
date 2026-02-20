package com.pfplaybackend.api.user.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NicknameTest {

    @Test
    @DisplayName("유효한 닉네임 생성")
    void validNickname() {
        Nickname nickname = new Nickname("Player1");
        assertThat(nickname.value()).isEqualTo("Player1");
    }

    @Test
    @DisplayName("최대 20자 닉네임 허용")
    void maxLengthNickname() {
        String twentyChars = "12345678901234567890";
        Nickname nickname = new Nickname(twentyChars);
        assertThat(nickname.value()).hasSize(20);
    }

    @Test
    @DisplayName("null 닉네임은 예외 발생")
    void nullNickname() {
        assertThatThrownBy(() -> new Nickname(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("빈 문자열 닉네임은 예외 발생")
    void emptyNickname() {
        assertThatThrownBy(() -> new Nickname(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("공백만 있는 닉네임은 예외 발생")
    void blankNickname() {
        assertThatThrownBy(() -> new Nickname("   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("21자 이상 닉네임은 예외 발생")
    void tooLongNickname() {
        assertThatThrownBy(() -> new Nickname("123456789012345678901"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("record equals 동작")
    void equalsAndHashCode() {
        Nickname n1 = new Nickname("Player1");
        Nickname n2 = new Nickname("Player1");
        Nickname n3 = new Nickname("Player2");

        assertThat(n1).isEqualTo(n2);
        assertThat(n1).isNotEqualTo(n3);
        assertThat(n1.hashCode()).isEqualTo(n2.hashCode());
    }
}
