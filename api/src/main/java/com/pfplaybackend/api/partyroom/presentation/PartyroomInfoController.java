package com.pfplaybackend.api.partyroom.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/partyrooms")
@RequiredArgsConstructor
public class PartyroomInfoController {

    /**
     * 모든 파티룸의 정보를 조회한다.
     */
    @GetMapping
    public void getPartyrooms() {
    }

    /**
     * 특정 파티룸의 일반 정보를 조회한다.
     */
    @GetMapping("/{partyroomId}")
    public void getPartyroomInfo(@PathVariable Long partyroomId) {
    }

    /**
     * 특정 파티룸 내의 파티원 목록을 조회한다.
     */
    @GetMapping("/{partyroomId}/partymembers")
    public void getPartymembers(@PathVariable Long partyroomId) {
    }
}
