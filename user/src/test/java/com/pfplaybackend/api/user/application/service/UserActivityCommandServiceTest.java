package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.user.domain.entity.data.ActivityData;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.adapter.out.persistence.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserActivityCommandServiceTest {

    @Mock MemberRepository memberRepository;
    @InjectMocks UserActivityCommandService userActivityCommandService;

    @Test
    @DisplayName("createUserActivities — 모든 ActivityType에 대해 활동 데이터를 생성한다")
    void createUserActivities_createsAllActivityTypes() {
        // given
        UserId userId = new UserId(1L);

        // when
        Map<ActivityType, ActivityData> result = userActivityCommandService.createUserActivities(userId);

        // then
        assertThat(result).hasSize(ActivityType.values().length);
        for (ActivityType type : ActivityType.values()) {
            assertThat(result).containsKey(type);
            assertThat(result.get(type).getActivityType()).isEqualTo(type);
            assertThat(result.get(type).getUserId()).isEqualTo(userId);
        }
    }
}
