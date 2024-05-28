package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.partyroom.application.service.PartyRoomManagementService;
import com.pfplaybackend.api.partyroom.application.service.impl.PartyRoomManagementServiceImpl;
import com.pfplaybackend.api.partyroom.domain.model.entity.domain.PartyroomDomain;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 이 클래스는 파티룸의 기본적인 생애주기(생성/삭제/조회)를 관리하는 표현 계층을 담당한다.
 */
@RestController
@RequestMapping("/api/v1/partyrooms")
@RequiredArgsConstructor
public class PartyroomManagementController {

    private final PartyRoomManagementService partyRoomManagementService;

    @PostMapping
    public ResponseEntity<PartyroomDomain> createPartyroom() {
        PartyroomDomain partyRoom = partyRoomManagementService.createPartyRoom();
        return ResponseEntity.ok(partyRoom);
    }

    @DeleteMapping("/{partyroomId}")
    public void deletePartyroom(@PathVariable Long partyroomId) {
    }
}
