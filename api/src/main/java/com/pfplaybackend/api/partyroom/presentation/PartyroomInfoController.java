package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.partyroom.application.service.PartyroomInfoService;
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

    }

    /**
     * 특정 파티룸의 요약 정보를 조회한다.
     * → 대기실에서 파티룸의 Hover 시 호출 API
     *
     * @param partyroomId
     */
    @GetMapping("/{partyroomId}/summary")
    public void getPartyroomInfoSummary(@PathVariable Long partyroomId) {

    }

    /**
     * 특정 파티룸의 일반 정보를 조회한다.
     * 파티룸 내에서 '파티 정보' 클릭 시 호출
     */
    @GetMapping("/{partyroomId}")
    public void getPartyroomInfo(@PathVariable Long partyroomId) {

    }

    /**
     * 특정 파티룸 내의 파티원 목록을 조회한다.
     * 파티룸 내에서
     */
    @GetMapping("/{partyroomId}/partymembers")
    public void getPartymembers(@PathVariable Long partyroomId) {

    }
}
