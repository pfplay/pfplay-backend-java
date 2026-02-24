package com.pfplaybackend.api.party.domain.entity.data;

import com.pfplaybackend.api.common.domain.value.PlaylistId;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DjDataTest {

    @Test
    @DisplayName("create — 팩토리 메서드가 모든 필드를 설정한다")
    void createSetsAllFields() {
        // given
        PartyroomId partyroomId = new PartyroomId(1L);
        PlaylistId playlistId = new PlaylistId(10L);
        CrewId crewId = new CrewId(20L);

        // when
        DjData dj = DjData.create(partyroomId, playlistId, crewId, 3);

        // then
        assertThat(dj.getPartyroomId()).isEqualTo(partyroomId);
        assertThat(dj.getPlaylistId()).isEqualTo(playlistId);
        assertThat(dj.getCrewId()).isEqualTo(crewId);
        assertThat(dj.getOrderNumber()).isEqualTo(3);
    }

    @Test
    @DisplayName("updateOrderNumber — 순서가 업데이트된다")
    void updateOrderNumberUpdatesOrder() {
        // given
        DjData dj = DjData.create(new PartyroomId(1L), new PlaylistId(10L), new CrewId(20L), 1);

        // when
        dj.updateOrderNumber(5);

        // then
        assertThat(dj.getOrderNumber()).isEqualTo(5);
    }
}
