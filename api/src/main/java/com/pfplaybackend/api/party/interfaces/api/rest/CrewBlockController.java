package com.pfplaybackend.api.party.interfaces.api.rest;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.party.application.service.CrewBlockService;
import com.pfplaybackend.api.party.interfaces.api.rest.payload.request.AddBlockRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Crew API")
@RequestMapping("/api/v1/crews")
@RestController
@RequiredArgsConstructor
public class CrewBlockController {

    final private CrewBlockService crewBlockService;

    @GetMapping("/me/blocks")
    public ResponseEntity<?> getBlockCrews()  {
        return ResponseEntity.ok()
                .body(ApiCommonResponse.success(crewBlockService.getBlocks()));
    }

    @PostMapping("/me/blocks")
    public ResponseEntity<?> blockOtherCrew(@RequestBody AddBlockRequest request)  {
        crewBlockService.addBlock(request);
        return ResponseEntity.ok()
                .body(ApiCommonResponse.success("OK"));
    }

    @DeleteMapping("/me/blocks/{blockId}")
    public ResponseEntity<?> unblockOther(@PathVariable Long blockId)  {
        crewBlockService.removeBlock(blockId);
        return ResponseEntity.ok()
                .body(ApiCommonResponse.success("OK"));
    }
}
