package com.pfplaybackend.api.admin.application.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NicknameGeneratorTest {

    @Test
    @DisplayName("generate \u2014 \ub2c9\ub124\uc784\uc774 null\uc774 \uc544\ub2c8\uace0 \ube44\uc5b4\uc788\uc9c0 \uc54a\ub2e4")
    void generateNotNullAndNotEmpty() {
        // given & when
        String nickname = NicknameGenerator.generate();

        // then
        assertThat(nickname).isNotNull();
        assertThat(nickname).isNotEmpty();
    }

    @Test
    @DisplayName("generate \u2014 \ub2c9\ub124\uc784\uc774 PascalCase \ud615\uc2dd\uc774\ub2e4")
    void generatePascalCaseFormat() {
        // given & when
        String nickname = NicknameGenerator.generate();

        // then
        assertThat(nickname.charAt(0)).isUpperCase();
    }

    @Test
    @DisplayName("generateWithSuffix \u2014 \uc811\ubbf8\uc0ac\uac00 \ud3ec\ud568\ub41c \ub2c9\ub124\uc784\uc744 \uc0dd\uc131\ud55c\ub2e4")
    void generateWithSuffixContainsSuffix() {
        // given
        int suffix = 123;

        // when
        String nickname = NicknameGenerator.generateWithSuffix(suffix);

        // then
        assertThat(nickname).endsWith("123");
    }

    @Test
    @DisplayName("generateUnique \u2014 \uc778\ub371\uc2a4\uac00 \ud3ec\ud568\ub41c \uace0\uc720 \ub2c9\ub124\uc784\uc744 \uc0dd\uc131\ud55c\ub2e4")
    void generateUniqueContainsIndex() {
        // given
        int index = 1;

        // when
        String nickname = NicknameGenerator.generateUnique(index);

        // then
        assertThat(nickname).endsWith("1");
    }

    @Test
    @DisplayName("generateUnique \u2014 \ucd5c\ub300 \uae38\uc774\uac00 17\uc790\ub97c \ub118\uc9c0 \uc54a\ub294\ub2e4")
    void generateUniqueMaxLength() {
        // given
        int maxIndex = 410;

        // when
        String nickname = NicknameGenerator.generateUnique(maxIndex);

        // then
        assertThat(nickname.length()).isLessThanOrEqualTo(17);
    }
}
