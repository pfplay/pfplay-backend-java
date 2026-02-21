package com.pfplaybackend.api.admin.adapter.in.web;

import com.pfplaybackend.api.admin.application.service.AdminPartyroomService;
import com.pfplaybackend.api.admin.application.dto.command.AdminCreatePartyroomCommand;
import com.pfplaybackend.api.admin.application.dto.command.BulkPreviewCommand;
import com.pfplaybackend.api.admin.application.dto.result.AdminPartyroomResult;
import com.pfplaybackend.api.admin.application.dto.result.BulkPreviewResult;
import com.pfplaybackend.api.admin.adapter.in.web.dto.request.AdminCreatePartyroomRequest;
import com.pfplaybackend.api.admin.adapter.in.web.dto.request.BulkPreviewEnvironmentRequest;
import com.pfplaybackend.api.admin.adapter.in.web.dto.response.AdminPartyroomResponse;
import com.pfplaybackend.api.admin.adapter.in.web.dto.response.BulkPreviewEnvironmentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Slf4j
@Tag(name = "Admin Partyroom API", description = "Admin operations for partyroom management")
@RestController
@RequestMapping("/api/v1/admin/partyrooms")
@RequiredArgsConstructor
public class AdminPartyroomController {

    private final AdminPartyroomService adminPartyroomService;

    @Operation(summary = "Create partyroom with designated host")
    @PostMapping
    @PreAuthorize("hasAuthority('FM')")
    public ResponseEntity<AdminPartyroomResponse> createPartyroom(
            @Valid @RequestBody AdminCreatePartyroomRequest request) {

        log.info("Admin creating partyroom: hostUserId={}, title={}",
                request.getHostUserId(), request.getTitle());

        AdminCreatePartyroomCommand command = new AdminCreatePartyroomCommand(
                request.getHostUserId(), request.getTitle(), request.getIntroduction(),
                request.getLinkDomain(), request.getPlaybackTimeLimit());

        AdminPartyroomResult result = adminPartyroomService.createPartyroomWithHost(command);

        AdminPartyroomResponse response = AdminPartyroomResponse.builder()
                .partyroomId(result.partyroomId())
                .hostUserId(result.hostUserId())
                .title(result.title())
                .introduction(result.introduction())
                .linkDomain(result.linkDomain())
                .playbackTimeLimit(result.playbackTimeLimit())
                .stageType(result.stageType())
                .isActive(result.isActive())
                .createdAt(result.createdAt())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Create bulk preview environment")
    @PostMapping("/bulk-preview")
    @PreAuthorize("hasAuthority('FM')")
    public ResponseEntity<BulkPreviewEnvironmentResponse> createBulkPreviewEnvironment(
            @Valid @RequestBody BulkPreviewEnvironmentRequest request) {

        log.info("Admin creating bulk preview environment: {} partyrooms with {} users each",
                request.getPartyroomCount(), request.getUsersPerRoom());

        BulkPreviewCommand command = new BulkPreviewCommand(
                request.getPartyroomCount(), request.getUsersPerRoom(), request.getTitlePrefix(),
                request.getIntroduction(), request.getLinkDomainPrefix(), request.getPlaybackTimeLimit());

        BulkPreviewResult result = adminPartyroomService.createBulkPreviewEnvironment(command);

        BulkPreviewEnvironmentResponse response = BulkPreviewEnvironmentResponse.builder()
                .totalPartyrooms(result.totalPartyrooms())
                .totalVirtualMembers(result.totalVirtualMembers())
                .executionTimeMs(result.executionTimeMs())
                .partyrooms(result.partyrooms().stream()
                        .map(s -> BulkPreviewEnvironmentResponse.PartyroomSummary.builder()
                                .partyroomId(s.partyroomId())
                                .title(s.title())
                                .linkDomain(s.linkDomain())
                                .hostUserId(s.hostUserId())
                                .crewCount(s.crewCount())
                                .crewUserIds(s.crewUserIds())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
