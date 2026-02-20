package com.pfplaybackend.api.user.domain.service;

import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.user.application.service.AvatarResourceService;
import com.pfplaybackend.api.user.domain.entity.data.ActivityData;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.domain.enums.ObtainmentType;
import com.pfplaybackend.api.user.domain.value.Score;
import com.pfplaybackend.api.common.domain.value.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserAvatarDomainServiceTest {

    @Mock
    AvatarResourceService avatarResourceService;

    @InjectMocks
    UserAvatarDomainService userAvatarDomainService;

    private Map<ActivityType, ActivityData> createActivityMap(ActivityType type, int score) {
        Map<ActivityType, ActivityData> map = new HashMap<>();
        map.put(type, ActivityData.create(new UserId(1L), type, score));
        return map;
    }

    private AvatarBodyDto createAvatarBodyDto(ObtainmentType type, int requiredScore) {
        return AvatarBodyDto.builder()
                .name("test_body")
                .resourceUri("uri")
                .obtainableType(type)
                .obtainableScore(requiredScore)
                .isCombinable(false)
                .isDefaultSetting(false)
                .isAvailable(false)
                .combinePositionX(0)
                .combinePositionY(0)
                .build();
    }

    @Test
    @DisplayName("isAvailableBody — BASIC 타입 아바타는 항상 사용 가능하다")
    void isAvailableBody_basic_alwaysTrue() {
        // given
        AvatarBodyDto dto = createAvatarBodyDto(ObtainmentType.BASIC, 0);
        Map<ActivityType, ActivityData> activityMap = createActivityMap(ActivityType.DJ_PNT, 0);

        // when
        boolean result = userAvatarDomainService.isAvailableBody(dto, activityMap);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isAvailableBody — DJ_PNT 타입이고 점수가 충분하면 사용 가능하다")
    void isAvailableBody_djPnt_sufficient() {
        // given
        AvatarBodyDto dto = createAvatarBodyDto(ObtainmentType.DJ_PNT, 50);
        Map<ActivityType, ActivityData> activityMap = createActivityMap(ActivityType.DJ_PNT, 100);

        // when
        boolean result = userAvatarDomainService.isAvailableBody(dto, activityMap);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isAvailableBody — DJ_PNT 타입이고 점수가 부족하면 사용 불가능하다")
    void isAvailableBody_djPnt_insufficient() {
        // given
        AvatarBodyDto dto = createAvatarBodyDto(ObtainmentType.DJ_PNT, 50);
        Map<ActivityType, ActivityData> activityMap = createActivityMap(ActivityType.DJ_PNT, 30);

        // when
        boolean result = userAvatarDomainService.isAvailableBody(dto, activityMap);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isAvailableBody — 점수가 정확히 같으면 사용 가능하다")
    void isAvailableBody_exactScore_boundary() {
        // given
        AvatarBodyDto dto = createAvatarBodyDto(ObtainmentType.DJ_PNT, 50);
        Map<ActivityType, ActivityData> activityMap = createActivityMap(ActivityType.DJ_PNT, 50);

        // when
        boolean result = userAvatarDomainService.isAvailableBody(dto, activityMap);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isAvailableBody — ROOM_ACT 타입이고 점수가 부족하면 사용 불가능하다")
    void isAvailableBody_roomAct_insufficient() {
        // given
        AvatarBodyDto dto = createAvatarBodyDto(ObtainmentType.ROOM_ACT, 100);
        Map<ActivityType, ActivityData> activityMap = createActivityMap(ActivityType.ROOM_ACT, 50);

        // when
        boolean result = userAvatarDomainService.isAvailableBody(dto, activityMap);

        // then
        assertThat(result).isFalse();
    }
}
