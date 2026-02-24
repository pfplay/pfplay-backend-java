package com.pfplaybackend.api.playlist.domain.entity.data;

import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlaylistDataTest {

    @Test
    @DisplayName("create — 팩토리 메서드로 생성 시 모든 필드가 올바르게 설정된다")
    void createPlaylist() {
        // given
        UserId ownerId = new UserId(100L);
        String name = "My Playlist";
        int orderNumber = 1;

        // when
        PlaylistData playlist = PlaylistData.create(orderNumber, name, PlaylistType.PLAYLIST, ownerId);

        // then
        assertThat(playlist.getOwnerId()).isEqualTo(ownerId);
        assertThat(playlist.getName()).isEqualTo(name);
        assertThat(playlist.getOrderNumber()).isEqualTo(orderNumber);
        assertThat(playlist.getType()).isEqualTo(PlaylistType.PLAYLIST);
    }

    @Test
    @DisplayName("create — GRABLIST 타입으로 생성할 수 있다")
    void createGrablist() {
        // given
        UserId ownerId = new UserId(200L);

        // when
        PlaylistData playlist = PlaylistData.create(0, "Grab", PlaylistType.GRABLIST, ownerId);

        // then
        assertThat(playlist.getType()).isEqualTo(PlaylistType.GRABLIST);
    }

    @Test
    @DisplayName("rename — 플레이리스트 이름 변경 시 name이 갱신된다")
    void rename() {
        // given
        PlaylistData playlist = PlaylistData.create(1, "Old Name", PlaylistType.PLAYLIST, new UserId(100L));

        // when
        playlist.rename("New Name");

        // then
        assertThat(playlist.getName()).isEqualTo("New Name");
    }
}
