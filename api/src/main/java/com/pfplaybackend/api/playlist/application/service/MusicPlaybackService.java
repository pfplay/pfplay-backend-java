package com.pfplaybackend.api.playlist.application.service;

import org.springframework.stereotype.Service;

@Service
public class MusicPlaybackService {

    public void getMusicToPlayback(Long playlistId) {
        // TODO playlistId 의 플레이리스트 내의 '곡' 중 order 가 1인 Music 을 조회한다.

        // 재생시킬 뮤직을 반환한 뒤에는 곡 순서를 원형 형태로 한칸씩 재배치해야 한다.
        // TODO 도메인 객체는 단순히 이를 ArrayList 자료구조로 관리하는 것보다 Linked 가 유리하다.

    }

    private void adjustOrders() {

    }
}
