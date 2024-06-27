package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.partyroom.application.service.PartyroomInfoService;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Partyroom API")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class PartyroomInfoController {

    private final PartyroomInfoService partyroomInfoService;

    /**
     * 모든 파티룸의 정보를 조회한다.
     * → 대기실에서의 목록 조회 API
     */
    @GetMapping
    public void getPartyrooms() {
        partyroomInfoService.getAllPartyrooms();
    }

    /**
     * 특정 파티룸의 요약 정보를 조회한다.
     * → 대기실에서 파티룸 엘리먼트에 마우스 Hover 시 호출 API
     *
     * @param partyroomId
     */
    @GetMapping("/{partyroomId}/summary")
    public void getPartyroomInfoSummary(@PathVariable Long partyroomId) {
        partyroomInfoService.getSummaryInfo(new PartyroomId(partyroomId));
    }

    /**
     * 특정 파티룸의 일반 정보를 조회한다.
     * → 파티룸 내에서 우측 상단의 '파티 정보' 클릭 시 호출 API
     */
    @GetMapping("/{partyroomId}")
    public void getPartyroomInfo(@PathVariable Long partyroomId) {
        partyroomInfoService.getGeneralInfo(new PartyroomId(partyroomId));
    }

    /**
     * 특정 파티룸 내의 '활동중인 파티원 목록'을 조회한다.
     * → 파티룸 입장 시 초기화 정보 조회 목적
     */
    @GetMapping("/{partyroomId}/partymembers")
    public void getPartymembers(@PathVariable Long partyroomId) {
        partyroomInfoService.getPartymembers(new PartyroomId(partyroomId));
    }
}
