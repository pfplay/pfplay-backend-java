package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.party.adapter.in.web.payload.response.info.QueryPartyroomListResponse.PartyroomElement;
import com.pfplaybackend.api.party.application.dto.partyroom.PartyroomWithCrewDto;
import com.pfplaybackend.api.party.application.dto.result.DjQueueInfoResult;
import com.pfplaybackend.api.party.application.dto.result.PartyroomSummaryResult;
import com.pfplaybackend.api.party.application.service.PartyroomQueryService;
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
public class PartyroomQueryController {

    private final PartyroomQueryService partyroomQueryService;

    /**
     * 모든 파티룸의 정보를 조회한다.
     * → 대기실에서의 목록 조회 API
     */
    @GetMapping
    public ResponseEntity<ApiCommonResponse<List<PartyroomElement>>> getPartyrooms() {
        List<PartyroomWithCrewDto> partyrooms = partyroomQueryService.getAllPartyrooms();
        Map<UserId, ProfileSettingDto> avatarSettings = partyroomQueryService.getPrimariesAvatarSettings(partyrooms);
        return ResponseEntity.ok().body(ApiCommonResponse.success(QueryPartyroomListResponse.from(partyrooms, avatarSettings)));
    }

    /**
     * 특정 파티룸의 '일반 정보'를 조회한다.
     * → 파티룸 내에서 우측 상단의 '파티 정보' 클릭 시 호출 API
     */
    @GetMapping("/{partyroomId}/summary")
    public ResponseEntity<ApiCommonResponse<PartyroomSummaryResult>> getPartyroomSummaryInfo(@PathVariable Long partyroomId) {
        return ResponseEntity.ok().body(ApiCommonResponse.success(partyroomQueryService.getSummaryInfo(new PartyroomId(partyroomId))));
    }

    @GetMapping("/{partyroomId}/dj-queue")
    public ResponseEntity<ApiCommonResponse<QueryDjQueueResponse>> getDjQueueInfo(@PathVariable Long partyroomId) {
        DjQueueInfoResult result = partyroomQueryService.getDjQueueInfo(new PartyroomId(partyroomId));
        return ResponseEntity.ok().body(ApiCommonResponse.success(
                QueryDjQueueResponse.from(result.playbackActivated(), result.queueStatus(),
                        result.registered(), result.currentPlayback(), result.djs())));
    }
}
