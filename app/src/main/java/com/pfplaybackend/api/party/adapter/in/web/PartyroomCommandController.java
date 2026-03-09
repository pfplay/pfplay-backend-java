package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.config.swagger.ApiErrorCodes;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.management.CreatePartyroomRequest;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.management.UpdateDjQueueStatusRequest;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.management.UpdatePartyroomRequest;
import com.pfplaybackend.api.party.adapter.in.web.payload.response.management.CreatePartyroomResponse;
import com.pfplaybackend.api.party.application.dto.command.CreatePartyroomCommand;
import com.pfplaybackend.api.party.application.dto.command.UpdateDjQueueStatusCommand;
import com.pfplaybackend.api.party.application.dto.command.UpdatePartyroomCommand;
import com.pfplaybackend.api.party.application.service.PartyroomCommandService;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @Operation(summary = "파티룸 생성", description = "새로운 파티룸을 생성합니다. 생성자는 자동으로 HOST 등급이 부여됩니다.")
    @ApiResponse(responseCode = "201", description = "파티룸 생성 성공")
    @SecurityRequirement(name = "cookieAuth")
    @ApiErrorCodes({PartyroomException.class})
    @PostMapping
    public ResponseEntity<ApiCommonResponse<CreatePartyroomResponse>> createPartyroom(@RequestBody CreatePartyroomRequest request) {
        PartyroomData partyRoom = partyroomCommandService.createGeneralPartyRoom(
                new CreatePartyroomCommand(request.getTitle(), request.getIntroduction(), request.getLinkDomain(), request.getPlaybackTimeLimit()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiCommonResponse.success(CreatePartyroomResponse.from(partyRoom)));
    }

    @Operation(summary = "파티룸 정보 수정", description = "파티룸의 제목, 소개, 링크 도메인, 재생 시간 제한 등의 정보를 수정합니다.")
    @ApiResponse(responseCode = "204", description = "파티룸 수정 성공")
    @SecurityRequirement(name = "cookieAuth")
    @ApiErrorCodes({PartyroomException.class})
    @PutMapping("/{partyroomId}")
    public ResponseEntity<Void> updatePartyroom(
            @Parameter(description = "파티룸 ID") @PathVariable Long partyroomId,
            @RequestBody UpdatePartyroomRequest request) {
        partyroomCommandService.updatePartyroom(new PartyroomId(partyroomId),
                new UpdatePartyroomCommand(request.getTitle(), request.getIntroduction(), request.getLinkDomain(), request.getPlaybackTimeLimit()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "파티룸 삭제", description = "파티룸을 삭제합니다. HOST 권한이 필요합니다.")
    @ApiResponse(responseCode = "204", description = "파티룸 삭제 성공")
    @SecurityRequirement(name = "cookieAuth")
    @ApiErrorCodes({PartyroomException.class})
    @DeleteMapping("/{partyroomId}")
    public ResponseEntity<Void> deletePartyroom(
            @Parameter(description = "파티룸 ID") @PathVariable Long partyroomId) {
        partyroomCommandService.deletePartyRoom(new PartyroomId(partyroomId));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "DJ 큐 상태 변경", description = "파티룸의 DJ 큐 활성화/비활성화 상태를 변경합니다. 파티룸 운영진만 호출 가능합니다.")
    @ApiResponse(responseCode = "204", description = "DJ 큐 상태 변경 성공")
    @SecurityRequirement(name = "cookieAuth")
    @ApiErrorCodes({PartyroomException.class})
    @PutMapping("/{partyroomId}/dj-queue")
    public ResponseEntity<Void> updateDjQueue(
            @Parameter(description = "파티룸 ID") @PathVariable Long partyroomId,
            @RequestBody UpdateDjQueueStatusRequest request) {
        partyroomCommandService.updateDjQueueStatus(new PartyroomId(partyroomId),
                new UpdateDjQueueStatusCommand(request.getQueueStatus()));
        return ResponseEntity.noContent().build();
    }
}
