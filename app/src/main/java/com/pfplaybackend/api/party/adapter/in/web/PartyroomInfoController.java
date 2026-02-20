package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.party.adapter.in.web.dto.PartyroomElement;
import com.pfplaybackend.api.party.adapter.out.persistence.PartyroomPlaybackRepository;
import com.pfplaybackend.api.party.application.dto.dj.DjWithProfileDto;
import com.pfplaybackend.api.party.application.dto.partyroom.PartyroomWithCrewDto;
import com.pfplaybackend.api.party.application.service.PartyroomInfoService;
import com.pfplaybackend.api.party.application.service.PlaybackInfoService;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomPlaybackData;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.party.domain.enums.QueueStatus;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.adapter.in.web.payload.response.info.*;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.common.domain.value.UserId;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Partyroom API")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class PartyroomInfoController {

    private final PartyroomInfoService partyroomInfoService;
    private final PlaybackInfoService playbackInfoService;
    private final PartyroomPlaybackRepository partyroomPlaybackRepository;

    /**
     * 모든 파티룸의 정보를 조회한다.
     * → 대기실에서의 목록 조회 API
     */
    @GetMapping
    public ResponseEntity<ApiCommonResponse<List<PartyroomElement>>> getPartyrooms() {
        List<PartyroomWithCrewDto> partyrooms = partyroomInfoService.getAllPartyrooms();
        Map<UserId, ProfileSettingDto> avatarSettings = partyroomInfoService.getPrimariesAvatarSettings(partyrooms);
        return ResponseEntity.ok().body(ApiCommonResponse.success(QueryPartyroomListResponse.from(partyrooms, avatarSettings)));
    }

    /**
     * 특정 파티룸의 '일반 정보'를 조회한다.
     * → 파티룸 내에서 우측 상단의 '파티 정보' 클릭 시 호출 API
     */
    @GetMapping("/{partyroomId}/summary")
    public ResponseEntity<ApiCommonResponse<QueryPartyroomSummaryResponse>> getPartyroomSummaryInfo(@PathVariable Long partyroomId) {
        return ResponseEntity.ok().body(ApiCommonResponse.success(partyroomInfoService.getSummaryInfo(new PartyroomId(partyroomId))));
    }

    @GetMapping("/{partyroomId}/dj-queue")
    public ResponseEntity<ApiCommonResponse<QueryDjQueueResponse>> getDjQueueInfo(@PathVariable Long partyroomId) {
        PartyroomData partyroom = partyroomInfoService.getPartyroomById(new PartyroomId(partyroomId));
        PartyroomPlaybackData playbackState = partyroomPlaybackRepository.findById(partyroom.getId()).orElseThrow();
        boolean isPlaybackActivated = playbackState.isActivated();
        QueueStatus queueStatus = partyroom.isQueueClosed() ? QueueStatus.CLOSE :  QueueStatus.OPEN;
        boolean isRegistered = partyroomInfoService.isAlreadyRegistered(partyroom.getId());
        PlaybackData playback = null;
        if(isPlaybackActivated) {
            playback = playbackInfoService.getPlaybackById(playbackState.getCurrentPlaybackId());
        }
        List<DjWithProfileDto> djWithProfiles = partyroomInfoService.getDjs(partyroom.getId());
        return ResponseEntity.ok().body(ApiCommonResponse.success(QueryDjQueueResponse.from(isPlaybackActivated, queueStatus, isRegistered, playback, djWithProfiles)));
    }
}
