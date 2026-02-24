package com.pfplaybackend.api.admin.adapter.in.web;

import com.pfplaybackend.api.admin.adapter.in.web.payload.request.InitializeDemoEnvironmentRequest;
import com.pfplaybackend.api.admin.adapter.in.web.payload.request.SimulateReactionsRequest;
import com.pfplaybackend.api.admin.adapter.in.web.payload.request.StartChatSimulationRequest;
import com.pfplaybackend.api.admin.adapter.in.web.payload.response.InitializeDemoEnvironmentResponse;
import com.pfplaybackend.api.admin.adapter.in.web.payload.response.QueryAdminPartyroomListResponse;
import com.pfplaybackend.api.admin.adapter.in.web.payload.response.QueryDemoEnvironmentStatusResponse;
import com.pfplaybackend.api.admin.adapter.in.web.payload.response.SimulateReactionsResponse;
import com.pfplaybackend.api.admin.application.dto.command.InitializeDemoCommand;
import com.pfplaybackend.api.admin.application.dto.result.AdminPartyroomListResult;
import com.pfplaybackend.api.admin.application.dto.result.DemoEnvironmentResult;
import com.pfplaybackend.api.admin.application.dto.result.DemoStatusResult;
import com.pfplaybackend.api.admin.application.dto.result.SimulateReactionsResult;
import com.pfplaybackend.api.admin.application.service.AdminDemoService;
import com.pfplaybackend.api.admin.application.service.AdminPartyroomService;
import com.pfplaybackend.api.admin.application.service.ChatSimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "Admin Demo API", description = "Demo environment initialization and simulation")
@RestController
@RequestMapping("/api/v1/admin/demo")
@RequiredArgsConstructor
public class AdminDemoController {

    private final AdminDemoService adminDemoService;
    private final AdminPartyroomService adminPartyroomService;
    private final ChatSimulationService chatSimulationService;

    @Operation(summary = "Check demo environment status")
    @GetMapping("/status")
    public ResponseEntity<QueryDemoEnvironmentStatusResponse> getDemoEnvironmentStatus() {
        DemoStatusResult result = adminDemoService.getDemoEnvironmentStatus();
        return ResponseEntity.ok(QueryDemoEnvironmentStatusResponse.builder()
                .initialized(result.initialized())
                .virtualMemberCount(result.virtualMemberCount())
                .generalRoomCount(result.generalRoomCount())
                .build());
    }

    @Operation(summary = "Get all active partyrooms")
    @GetMapping("/partyrooms")
    @PreAuthorize("hasAuthority('FM')")
    public ResponseEntity<QueryAdminPartyroomListResponse> getPartyrooms() {
        AdminPartyroomListResult result = adminDemoService.getPartyrooms();
        QueryAdminPartyroomListResponse response = QueryAdminPartyroomListResponse.builder()
                .partyrooms(result.partyrooms().stream()
                        .map(item -> QueryAdminPartyroomListResponse.PartyroomItem.builder()
                                .partyroomId(item.partyroomId())
                                .stageType(item.stageType())
                                .title(item.title())
                                .linkDomain(item.linkDomain())
                                .crewCount(item.crewCount())
                                .djCount(item.djCount())
                                .isPlaybackActivated(item.playbackActivated())
                                .build())
                        .collect(Collectors.toList()))
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Initialize complete demo environment")
    @PostMapping("/init")
    @PreAuthorize("hasAuthority('FM')")
    public ResponseEntity<InitializeDemoEnvironmentResponse> initializeDemoEnvironment(
            @Valid @RequestBody InitializeDemoEnvironmentRequest request) {

        log.warn("!!! INITIALIZING FULL DEMO ENVIRONMENT !!!");

        InitializeDemoCommand command = new InitializeDemoCommand(
                request.getPlaybackTimeLimit(), request.getTitlePrefix(),
                request.getIntroduction(), request.getRegisterDjs());

        DemoEnvironmentResult result = adminDemoService.initializeDemoEnvironment(command);

        log.info("Demo environment initialized: {} members, {} partyrooms, {} DJs, {}ms",
                result.totalMembers(), result.totalPartyrooms(),
                result.totalDjsRegistered(), result.executionTimeMs());

        InitializeDemoEnvironmentResponse response = InitializeDemoEnvironmentResponse.builder()
                .totalMembers(result.totalMembers())
                .specialMembers(result.specialMembers())
                .totalPartyrooms(result.totalPartyrooms())
                .totalDjsRegistered(result.totalDjsRegistered())
                .executionTimeMs(result.executionTimeMs())
                .mainStage(toPartyroomDetail(result.mainStage()))
                .generalRooms(result.generalRooms().stream()
                        .map(this::toPartyroomDetail)
                        .collect(Collectors.toList()))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Simulate reactions in partyroom")
    @PostMapping("/partyrooms/{partyroomId}/reactions")
    @PreAuthorize("hasAuthority('FM')")
    public ResponseEntity<SimulateReactionsResponse> simulateReactions(
            @PathVariable Long partyroomId,
            @Valid @RequestBody SimulateReactionsRequest request) {

        log.info("Admin simulating reactions: partyroomId={}", partyroomId);
        SimulateReactionsResult result = adminPartyroomService.simulateReactions(partyroomId);

        SimulateReactionsResponse response = SimulateReactionsResponse.builder()
                .partyroomId(result.partyroomId())
                .playbackId(result.playbackId())
                .reactions(result.reactions().stream()
                        .map(r -> SimulateReactionsResponse.SimulatedReaction.builder()
                                .userId(r.userId())
                                .reactionType(r.reactionType())
                                .eventPublished(r.eventPublished())
                                .build())
                        .collect(Collectors.toList()))
                .aggregation(SimulateReactionsResponse.AggregationCounts.builder()
                        .likeCount(result.aggregation().likeCount())
                        .dislikeCount(result.aggregation().dislikeCount())
                        .grabCount(result.aggregation().grabCount())
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Start chat simulation")
    @PostMapping("/partyrooms/{partyroomId}/chat")
    @PreAuthorize("hasAuthority('FM')")
    public ResponseEntity<Map<String, Object>> startChatSimulation(
            @PathVariable Long partyroomId,
            @Valid @RequestBody StartChatSimulationRequest request) {

        log.info("Admin starting chat simulation: partyroomId={}, scriptType={}",
                partyroomId, request.getScriptType());

        chatSimulationService.startChatSimulation(partyroomId, request.getScriptType());

        Map<String, Object> response = new HashMap<>();
        response.put("partyroomId", partyroomId);
        response.put("status", "STARTED");
        response.put("scriptType", request.getScriptType());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Stop chat simulation")
    @DeleteMapping("/partyrooms/{partyroomId}/chat")
    @PreAuthorize("hasAuthority('FM')")
    public ResponseEntity<Map<String, Object>> stopChatSimulation(@PathVariable Long partyroomId) {

        log.info("Admin stopping chat simulation: partyroomId={}", partyroomId);
        chatSimulationService.stopChatSimulation(partyroomId);

        Map<String, Object> response = new HashMap<>();
        response.put("partyroomId", partyroomId);
        response.put("status", "STOPPED");

        return ResponseEntity.ok(response);
    }

    private InitializeDemoEnvironmentResponse.PartyroomDetail toPartyroomDetail(DemoEnvironmentResult.PartyroomDetail detail) {
        return InitializeDemoEnvironmentResponse.PartyroomDetail.builder()
                .partyroomId(detail.partyroomId())
                .stageType(detail.stageType())
                .title(detail.title())
                .linkDomain(detail.linkDomain())
                .hostUserId(detail.hostUserId())
                .totalCrewCount(detail.totalCrewCount())
                .djUserId(detail.djUserId())
                .playlistId(detail.playlistId())
                .build();
    }
}
