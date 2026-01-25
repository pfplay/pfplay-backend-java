package com.pfplaybackend.api.admin.presentation;

import com.pfplaybackend.api.admin.application.service.AdminDemoService;
import com.pfplaybackend.api.admin.application.service.AdminPartyroomService;
import com.pfplaybackend.api.admin.application.service.ChatSimulationService;
import com.pfplaybackend.api.admin.presentation.dto.request.InitializeDemoEnvironmentRequest;
import com.pfplaybackend.api.admin.presentation.dto.request.SimulateReactionsRequest;
import com.pfplaybackend.api.admin.presentation.dto.request.StartChatSimulationRequest;
import com.pfplaybackend.api.admin.presentation.dto.response.AdminPartyroomListResponse;
import com.pfplaybackend.api.admin.presentation.dto.response.DemoEnvironmentResponse;
import com.pfplaybackend.api.admin.presentation.dto.response.DemoEnvironmentStatusResponse;
import com.pfplaybackend.api.admin.presentation.dto.response.SimulateReactionsResponse;
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
    public ResponseEntity<DemoEnvironmentStatusResponse> getDemoEnvironmentStatus() {
        return ResponseEntity.ok(adminDemoService.getDemoEnvironmentStatus());
    }

    @Operation(summary = "Get all active partyrooms")
    @GetMapping("/partyrooms")
    @PreAuthorize("hasAuthority('FM')")
    public ResponseEntity<AdminPartyroomListResponse> getPartyrooms() {
        return ResponseEntity.ok(adminDemoService.getPartyrooms());
    }

    @Operation(summary = "Initialize complete demo environment")
    @PostMapping("/init")
    @PreAuthorize("hasAuthority('FM')")
    public ResponseEntity<DemoEnvironmentResponse> initializeDemoEnvironment(
            @Valid @RequestBody InitializeDemoEnvironmentRequest request) {

        log.warn("!!! INITIALIZING FULL DEMO ENVIRONMENT !!!");
        DemoEnvironmentResponse response = adminDemoService.initializeDemoEnvironment(request);

        log.info("Demo environment initialized: {} members, {} partyrooms, {} DJs, {}ms",
                response.getTotalMembers(),
                response.getTotalPartyrooms(),
                response.getTotalDjsRegistered(),
                response.getExecutionTimeMs());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Simulate reactions in partyroom")
    @PostMapping("/partyrooms/{partyroomId}/reactions")
    @PreAuthorize("hasAuthority('FM')")
    public ResponseEntity<SimulateReactionsResponse> simulateReactions(
            @PathVariable Long partyroomId,
            @Valid @RequestBody SimulateReactionsRequest request) {

        log.info("Admin simulating reactions: partyroomId={}", partyroomId);
        SimulateReactionsResponse response = adminPartyroomService.simulateReactions(partyroomId);

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
}
