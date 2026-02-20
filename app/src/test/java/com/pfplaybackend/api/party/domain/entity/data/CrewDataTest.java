package com.pfplaybackend.api.party.domain.entity.data;

import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.enums.StageType;
import com.pfplaybackend.api.party.domain.value.LinkDomain;
import com.pfplaybackend.api.party.domain.value.PlaybackTimeLimit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CrewDataTest {

    private PartyroomData createPartyroom() {
        return PartyroomData.create("Room", "intro", LinkDomain.of("youtube.com"),
                PlaybackTimeLimit.ofMinutes(5), StageType.GENERAL, new UserId(1L));
    }

    @Test
    @DisplayName("create — 팩토리 메서드로 생성 시 활성 상태이고 밴 상태가 아니다")
    void create_activeAndNotBanned() {
        // given
        PartyroomData partyroom = createPartyroom();
        UserId userId = new UserId(10L);

        // when
        CrewData crew = CrewData.create(partyroom, userId, GradeType.CLUBBER);

        // then
        assertThat(crew.isActive()).isTrue();
        assertThat(crew.isBanned()).isFalse();
        assertThat(crew.getEnteredAt()).isNotNull();
        assertThat(crew.getGradeType()).isEqualTo(GradeType.CLUBBER);
    }

    @Test
    @DisplayName("create — 팩토리 메서드로 생성 시 PartyroomData가 연결된다")
    void create_partyroomAssigned() {
        // given
        PartyroomData partyroom = createPartyroom();

        // when
        CrewData crew = CrewData.create(partyroom, new UserId(10L), GradeType.CLUBBER);

        // then
        assertThat(crew.getPartyroomData()).isSameAs(partyroom);
    }

    @Test
    @DisplayName("deactivatePresence — 퇴장 시 isActive가 false이고 exitedAt이 설정된다")
    void deactivatePresence() {
        // given
        CrewData crew = CrewData.create(createPartyroom(), new UserId(10L), GradeType.CLUBBER);

        // when
        crew.deactivatePresence();

        // then
        assertThat(crew.isActive()).isFalse();
        assertThat(crew.getExitedAt()).isNotNull();
    }

    @Test
    @DisplayName("activatePresence — 재입장 시 isActive가 true이고 enteredAt이 갱신된다")
    void activatePresence() {
        // given
        CrewData crew = CrewData.create(createPartyroom(), new UserId(10L), GradeType.CLUBBER);
        crew.deactivatePresence();

        // when
        crew.activatePresence();

        // then
        assertThat(crew.isActive()).isTrue();
        assertThat(crew.getEnteredAt()).isNotNull();
    }

    @Test
    @DisplayName("updateGrade — 등급 변경 시 gradeType이 업데이트된다")
    void updateGrade() {
        // given
        CrewData crew = CrewData.create(createPartyroom(), new UserId(10L), GradeType.CLUBBER);

        // when
        crew.updateGrade(GradeType.MODERATOR);

        // then
        assertThat(crew.getGradeType()).isEqualTo(GradeType.MODERATOR);
    }

    @Test
    @DisplayName("enforceBan — 밴 부과 시 isBanned이 true가 된다")
    void enforceBan() {
        // given
        CrewData crew = CrewData.create(createPartyroom(), new UserId(10L), GradeType.CLUBBER);

        // when
        crew.enforceBan();

        // then
        assertThat(crew.isBanned()).isTrue();
    }

    @Test
    @DisplayName("releaseBan — 밴 해제 시 isBanned이 false가 된다")
    void releaseBan() {
        // given
        CrewData crew = CrewData.create(createPartyroom(), new UserId(10L), GradeType.CLUBBER);
        crew.enforceBan();

        // when
        crew.releaseBan();

        // then
        assertThat(crew.isBanned()).isFalse();
    }
}
