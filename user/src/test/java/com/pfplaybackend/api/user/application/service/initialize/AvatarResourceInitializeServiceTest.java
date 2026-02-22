package com.pfplaybackend.api.user.application.service.initialize;

import com.pfplaybackend.api.user.adapter.out.persistence.AvatarBodyResourceRepository;
import com.pfplaybackend.api.user.adapter.out.persistence.AvatarFaceResourceRepository;
import com.pfplaybackend.api.user.adapter.out.persistence.AvatarIconResourceRepository;
import com.pfplaybackend.api.user.domain.entity.data.AvatarBodyResourceData;
import com.pfplaybackend.api.user.domain.entity.data.AvatarFaceResourceData;
import com.pfplaybackend.api.user.domain.entity.data.AvatarIconResourceData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvatarResourceInitializeServiceTest {

    @Mock AvatarBodyResourceRepository avatarBodyResourceRepository;
    @Mock AvatarFaceResourceRepository avatarFaceResourceRepository;
    @Mock AvatarIconResourceRepository avatarIconResourceRepository;

    @InjectMocks AvatarResourceInitializeService avatarResourceInitializeService;

    @Test
    @DisplayName("addAvatarBodies — 기존 리소스가 없으면 새로 생성한다")
    void addAvatarBodies_createsNew() {
        // given
        when(avatarBodyResourceRepository.findByName(anyString())).thenReturn(Optional.empty());

        // when
        avatarResourceInitializeService.addAvatarBodies();

        // then
        verify(avatarBodyResourceRepository, times(15)).save(any(AvatarBodyResourceData.class));
    }

    @Test
    @DisplayName("addAvatarBodies — 기존 리소스가 있으면 업데이트한다")
    void addAvatarBodies_updatesExisting() {
        // given
        AvatarBodyResourceData existing = mock(AvatarBodyResourceData.class);
        when(avatarBodyResourceRepository.findByName(anyString())).thenReturn(Optional.of(existing));

        // when
        avatarResourceInitializeService.addAvatarBodies();

        // then
        verify(existing, times(15)).updateResource(anyString(), any(), anyInt(), anyBoolean(), anyBoolean(), anyInt(), anyInt());
        verify(avatarBodyResourceRepository, times(15)).save(existing);
    }

    @Test
    @DisplayName("addAvatarFaces — 페이스 리소스를 저장한다")
    void addAvatarFaces_savesFaceResource() {
        // given — no preconditions needed

        // when
        avatarResourceInitializeService.addAvatarFaces();

        // then
        verify(avatarFaceResourceRepository, times(1)).save(any(AvatarFaceResourceData.class));
    }

    @Test
    @DisplayName("addAvatarIcons — 아이콘 리소스를 저장한다")
    void addAvatarIcons_savesIconResources() {
        // given — no preconditions needed

        // when
        avatarResourceInitializeService.addAvatarIcons();

        // then
        verify(avatarIconResourceRepository, times(5)).save(any(AvatarIconResourceData.class));
    }
}
