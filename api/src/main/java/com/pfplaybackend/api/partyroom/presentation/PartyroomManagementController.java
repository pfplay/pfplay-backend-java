package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.partyroom.application.service.PartyRoomManagementService;
import com.pfplaybackend.api.partyroom.domain.model.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.presentation.payload.request.CreatePartyroomRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 이 클래스는 파티룸의 기본적인 생애주기(생성/삭제/조회)를 관리하는 표현 계층을 담당한다.
 */
@Tag(name = "Partyroom Management API", description = "Operations related to partyroom management")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class PartyroomManagementController {

    private final PartyRoomManagementService partyRoomManagementService;

    @PostMapping
    public ResponseEntity<Partyroom> createPartyroom(CreatePartyroomRequest createPartyroomRequest) {
        Partyroom partyRoom = partyRoomManagementService.createGeneralPartyRoom(createPartyroomRequest);
        return ResponseEntity.ok(partyRoom);
    }

    @DeleteMapping("/{partyroomId}")
    public void deletePartyroom(@PathVariable Long partyroomId) {
        partyRoomManagementService.deletePartyRoom(new PartyroomId(partyroomId));
    }
}
