package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.management.CreatePartyroomRequest;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.management.UpdateDjQueueStatusRequest;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.management.UpdatePartyroomRequest;
import com.pfplaybackend.api.party.adapter.in.web.payload.response.management.CreatePartyroomResponse;
import com.pfplaybackend.api.party.application.dto.command.CreatePartyroomCommand;
import com.pfplaybackend.api.party.application.dto.command.UpdateDjQueueStatusCommand;
import com.pfplaybackend.api.party.application.dto.command.UpdatePartyroomCommand;
import com.pfplaybackend.api.party.application.service.PartyroomCommandService;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
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
public class PartyroomCommandController {

    private final PartyroomCommandService partyroomCommandService;

    @PostMapping
    public ResponseEntity<ApiCommonResponse<CreatePartyroomResponse>> createPartyroom(@RequestBody CreatePartyroomRequest request) {
        PartyroomData partyRoom = partyroomCommandService.createGeneralPartyRoom(
                new CreatePartyroomCommand(request.getTitle(), request.getIntroduction(), request.getLinkDomain(), request.getPlaybackTimeLimit()));
        return ResponseEntity.ok().body(ApiCommonResponse.success(CreatePartyroomResponse.from(partyRoom)));
    }

    @PutMapping("/{partyroomId}")
    public ResponseEntity<ApiCommonResponse<Void>> updatePartyroom(@PathVariable Long partyroomId, @RequestBody UpdatePartyroomRequest request) {
        partyroomCommandService.updatePartyroom(new PartyroomId(partyroomId),
                new UpdatePartyroomCommand(request.getTitle(), request.getIntroduction(), request.getLinkDomain(), request.getPlaybackTimeLimit()));
        return ResponseEntity.ok()
                .body(ApiCommonResponse.ok());
    }

    @DeleteMapping("/{partyroomId}")
    public ResponseEntity<Void> deletePartyroom(@PathVariable Long partyroomId) {
        partyroomCommandService.deletePartyRoom(new PartyroomId(partyroomId));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{partyroomId}/dj-queue")
    public ResponseEntity<ApiCommonResponse<Void>> updateDjQueue(@PathVariable Long partyroomId, @RequestBody UpdateDjQueueStatusRequest request) {
        partyroomCommandService.updateDjQueueStatus(new PartyroomId(partyroomId),
                new UpdateDjQueueStatusCommand(request.getQueueStatus()));
        return ResponseEntity.ok()
                .body(ApiCommonResponse.ok());
    }
}
