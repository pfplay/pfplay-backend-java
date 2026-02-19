package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.party.application.dto.dj.DjWithProfileDto;
import com.pfplaybackend.api.party.application.port.out.UserProfileQueryPort;
import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PlaylistId;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.DjRepository;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.domain.value.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartyroomInfoServiceGetDjsTest {

    @Mock private UserProfileQueryPort userProfileService;
    @Mock private CrewRepository crewRepository;
    @Mock private DjRepository djRepository;
    @InjectMocks private PartyroomInfoService partyroomInfoService;

    private DjData createDj(long crewId, int orderNumber, boolean isQueued, UserId userId) {
        return DjData.builder()
                .id(crewId * 100)
                .userId(userId)
                .crewId(new CrewId(crewId))
                .playlistId(new PlaylistId(1L))
                .orderNumber(orderNumber)
                .isQueued(isQueued)
                .build();
    }

    private ProfileSettingDto mockProfile(String nickname, String iconUri) {
        ProfileSettingDto dto = mock(ProfileSettingDto.class);
        when(dto.getNickname()).thenReturn(nickname);
        when(dto.getAvatarIconUri()).thenReturn(iconUri);
        return dto;
    }

    @Test
    @DisplayName("getDjs - isQueued가 false인 DJ는 결과에서 제외되어야 한다")
    void getDjs_shouldExcludeDequeuedDjs() {
        // given
        UserId user1 = new UserId();
        UserId user3 = new UserId();

        DjData queuedDj1 = createDj(1L, 1, true, user1);
        DjData queuedDj2 = createDj(3L, 2, true, user3);

        Long partyroomId = 1L;
        // Only queued DJs are returned by the repository
        when(djRepository.findByPartyroomDataIdAndIsQueuedTrueOrderByOrderNumberAsc(partyroomId))
                .thenReturn(List.of(queuedDj1, queuedDj2));

        Map<UserId, ProfileSettingDto> profileMap = new HashMap<>();
        profileMap.put(user1, mockProfile("nick1", "icon1"));
        profileMap.put(user3, mockProfile("nick3", "icon3"));
        when(userProfileService.getUsersProfileSetting(anyList())).thenReturn(profileMap);

        // when
        List<DjWithProfileDto> result = partyroomInfoService.getDjs(partyroomId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).noneMatch(dto -> dto.getCrewId() == 2L);
    }

    @Test
    @DisplayName("getDjs - orderNumber 기준으로 오름차순 정렬되어야 한다")
    void getDjs_shouldSortByOrderNumberAscending() {
        // given
        UserId user1 = new UserId();
        UserId user2 = new UserId();
        UserId user3 = new UserId();

        DjData dj1 = createDj(1L, 1, true, user1);
        DjData dj2 = createDj(2L, 2, true, user2);
        DjData dj3 = createDj(3L, 3, true, user3);

        Long partyroomId = 1L;
        // Repository returns in order
        when(djRepository.findByPartyroomDataIdAndIsQueuedTrueOrderByOrderNumberAsc(partyroomId))
                .thenReturn(List.of(dj1, dj2, dj3));

        Map<UserId, ProfileSettingDto> profileMap = new HashMap<>();
        profileMap.put(user1, mockProfile("nick1", "icon1"));
        profileMap.put(user2, mockProfile("nick2", "icon2"));
        profileMap.put(user3, mockProfile("nick3", "icon3"));
        when(userProfileService.getUsersProfileSetting(anyList())).thenReturn(profileMap);

        // when
        List<DjWithProfileDto> result = partyroomInfoService.getDjs(partyroomId);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getOrderNumber()).isEqualTo(1);
        assertThat(result.get(1).getOrderNumber()).isEqualTo(2);
        assertThat(result.get(2).getOrderNumber()).isEqualTo(3);
    }

    @Test
    @DisplayName("getDjs - 모든 DJ가 dequeued이면 빈 리스트를 반환해야 한다")
    void getDjs_shouldReturnEmptyList_whenAllDjsDequeued() {
        // given
        Long partyroomId = 1L;
        // Repository returns empty for all dequeued
        when(djRepository.findByPartyroomDataIdAndIsQueuedTrueOrderByOrderNumberAsc(partyroomId))
                .thenReturn(List.of());

        when(userProfileService.getUsersProfileSetting(anyList())).thenReturn(new HashMap<>());

        // when
        List<DjWithProfileDto> result = partyroomInfoService.getDjs(partyroomId);

        // then
        assertThat(result).isEmpty();
    }
}
