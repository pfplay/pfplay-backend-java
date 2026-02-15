package com.pfplaybackend.api.playlist.application.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class TrackCommandServiceTest {

    @Test
    @DisplayName("addTrackInPlaylist 메서드에 @Transactional이 선언되어 있어야 한다")
    void addTrackInPlaylist_shouldHaveTransactionalAnnotation() throws NoSuchMethodException {
        // given
        Method method = TrackCommandService.class.getDeclaredMethod(
                "addTrackInPlaylist", Long.class,
                com.pfplaybackend.api.playlist.presentation.payload.request.AddTrackRequest.class
        );

        // when & then
        boolean hasJakartaTransactional = method.isAnnotationPresent(Transactional.class);
        boolean hasSpringTransactional = method.isAnnotationPresent(
                org.springframework.transaction.annotation.Transactional.class
        );

        assertThat(hasJakartaTransactional || hasSpringTransactional)
                .as("addTrackInPlaylist should have @Transactional annotation")
                .isTrue();
    }
}
