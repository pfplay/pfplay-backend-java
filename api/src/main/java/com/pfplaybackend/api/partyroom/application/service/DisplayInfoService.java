package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.partyroom.repository.PlaybackRepository;
import com.pfplaybackend.api.partyroom.repository.history.PlaybackReactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DisplayInfoService {

    private final PlaybackRepository playbackRepository;
    private final PlaybackReactionHistoryRepository playbackReactionHistoryRepository;


    public void getDisplayInfo() {
        // TODO 현 파티룸의 currentPlaybackId 를 기준으로 playbackReactionHistory 를 조회한다.
        // 현재 재생곡(playbackID)에 대한 호출자 개인의 '리액션 선택 이력'을 조회한다. (1회)

        //


    }
}
