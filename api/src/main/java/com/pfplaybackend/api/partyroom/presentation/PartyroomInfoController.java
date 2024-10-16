package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.partyroom.application.dto.DisplayDto;
import com.pfplaybackend.api.partyroom.application.dto.DjWithProfileDto;
import com.pfplaybackend.api.partyroom.application.dto.PartymemberSetupDto;
import com.pfplaybackend.api.partyroom.application.dto.PartyroomWithMemberDto;
import com.pfplaybackend.api.partyroom.application.service.DisplayInfoService;
import com.pfplaybackend.api.partyroom.application.service.PartyroomInfoService;
import com.pfplaybackend.api.partyroom.application.service.PlaybackInfoService;
import com.pfplaybackend.api.partyroom.application.service.PlaybackReactionService;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Playback;
import com.pfplaybackend.api.partyroom.domain.enums.QueueStatus;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.presentation.dto.PartyroomElement;
import com.pfplaybackend.api.partyroom.presentation.payload.response.*;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.domain.value.UserId;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "Partyroom API")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class PartyroomInfoController {

    private final PartyroomInfoService partyroomInfoService;
    private final DisplayInfoService displayInfoService;
    private final PlaybackInfoService playbackInfoService;
    private final PlaybackReactionService playbackReactionService;


    /**
     * 모든 파티룸의 정보를 조회한다.
     * → 대기실에서의 목록 조회 API
     */
    @GetMapping
    public ResponseEntity<?> getPartyrooms() {
        List<PartyroomWithMemberDto> partyrooms = partyroomInfoService.getAllPartyrooms();
        Map<UserId, ProfileSettingDto> avatarSettings = partyroomInfoService.getPrimariesAvatarSettings(partyrooms);
        return ResponseEntity.ok().body( QueryPartyroomListResponse.from(partyrooms, avatarSettings));
    }

    /**
     * 특정 파티룸의 '일반 정보'를 조회한다.
     * → 파티룸 내에서 우측 상단의 '파티 정보' 클릭 시 호출 API
     */
    @GetMapping("/{partyroomId}/summary")
    public ResponseEntity<QueryPartyroomInfoResponse> getPartyroomSummaryInfo(@PathVariable Long partyroomId) {
        partyroomInfoService.getSummaryInfo(new PartyroomId(partyroomId));
        return null;
    }

    /**
     * 특정 파티룸 내의 '활동중인 파티원 목록'을 조회한다.
     * → 파티룸 입장 시 초기화 정보 조회 목적
     */
    @GetMapping("/{partyroomId}/partymembers")
    public ResponseEntity<QueryPartymemberListResponse> getPartymembers(@PathVariable Long partyroomId,
                                                                        @RequestParam(value = "groupByGrade", required = false, defaultValue = "false") boolean groupByGrade) {
        // Grade 별로 그루핑할 수 있는 옵션을 제공한다.
        partyroomInfoService.getPartymembers(new PartyroomId(partyroomId));
        return null;
    }

    @GetMapping("/{partyroomId}/setup")
    public ResponseEntity<?> getSetupInfo(@PathVariable Long partyroomId) {
        List<PartymemberSetupDto> memberDto = partyroomInfoService.getPartymembersForSetup(new PartyroomId(partyroomId));
        DisplayDto displayDto = displayInfoService.getDisplayInfo();
        return ResponseEntity.ok().body(QueryPartyroomSetupResponse.from(memberDto, displayDto));
    }

    @GetMapping("/{partyroomId}/playback/latest")
    public void getLatestPlaybackInfo(@PathVariable Long partyroomId) {
        // TODO 가장 최근 재생 이력 20건
    }

    @GetMapping("/{partyroomId}/dj-queue")
    public ResponseEntity<?> getDjQueueInfo(@PathVariable Long partyroomId) {
        Partyroom partyroom = partyroomInfoService.getById(new PartyroomId(partyroomId));
        boolean isPlaybackActivated = partyroom.isPlaybackActivated();
        QueueStatus queueStatus = partyroom.isQueueClosed() ? QueueStatus.CLOSE :  QueueStatus.OPEN;
        boolean isRegistered = partyroomInfoService.isAlreadyRegistered(partyroom);
        Playback playback = null;
        if(isPlaybackActivated) {
            playback = playbackInfoService.getPlaybackById(partyroom.getCurrentPlaybackId());
        }
        List<DjWithProfileDto> djWithProfiles = partyroomInfoService.getDjs(partyroom);
        return ResponseEntity.ok().body(QueryDjQueueResponse.from(isPlaybackActivated, queueStatus, isRegistered, playback, djWithProfiles));
    }
}
