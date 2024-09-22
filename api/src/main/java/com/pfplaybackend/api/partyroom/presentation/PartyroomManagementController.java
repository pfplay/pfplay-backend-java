package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.partyroom.application.service.PartyroomManagementService;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.presentation.payload.request.CreatePartyroomRequest;
import com.pfplaybackend.api.partyroom.presentation.payload.request.UpdateDjQueueStatusRequest;
import com.pfplaybackend.api.partyroom.presentation.payload.request.UpdatePartyroomRequest;
import com.pfplaybackend.api.partyroom.presentation.payload.response.CreatePartyroomResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 이 클래스는 파티룸의 기본적인 생애주기(생성/삭제/조회)를 관리하는 표현 계층을 담당한다.
 */
@Tag(name = "Partyroom API")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class PartyroomManagementController {

    private final PartyroomManagementService partyRoomManagementService;

    @PostMapping
    public ResponseEntity<?> createPartyroom(@RequestBody CreatePartyroomRequest createPartyroomRequest) {
        Partyroom partyRoom = partyRoomManagementService.createGeneralPartyRoom(createPartyroomRequest);
        return ResponseEntity.ok().body(CreatePartyroomResponse.from(partyRoom));
    }

    @PutMapping("/{partyroomId}")
    public ResponseEntity<?> updatePartyroom(@PathVariable Long partyroomId, @RequestBody UpdatePartyroomRequest request) {
        partyRoomManagementService.updatePartyroom(new PartyroomId(partyroomId), request);
        return ResponseEntity.ok()
                .body(ApiCommonResponse.success("OK"));
    }

    @DeleteMapping("/{partyroomId}")
    public void deletePartyroom(@PathVariable Long partyroomId) {
        partyRoomManagementService.deletePartyRoom(new PartyroomId(partyroomId));
    }

    @PutMapping("/{partyroomId}/dj-queue")
    public ResponseEntity<?> updateDjQueue(@PathVariable Long partyroomId, @RequestBody UpdateDjQueueStatusRequest request) {
        partyRoomManagementService.updateDjQueueStatus(new PartyroomId(partyroomId), request);
        return ResponseEntity.ok()
                .body(ApiCommonResponse.success("OK"));
    }
}
