package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.party.application.dto.result.BlockedCrewResult;
import com.pfplaybackend.api.party.application.service.CrewBlockService;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.AddBlockRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Crew API")
@RequestMapping("/api/v1/crews")
@RestController
@RequiredArgsConstructor
public class CrewBlockController {

    final private CrewBlockService crewBlockService;

    @GetMapping("/me/blocks")
    public ResponseEntity<ApiCommonResponse<List<BlockedCrewResult>>> getBlockCrews()  {
        return ResponseEntity.ok()
                .body(ApiCommonResponse.success(crewBlockService.getBlocks()));
    }

    @PostMapping("/me/blocks")
    public ResponseEntity<ApiCommonResponse<Void>> blockOtherCrew(@RequestBody AddBlockRequest request)  {
        crewBlockService.addBlock(request);
        return ResponseEntity.ok()
                .body(ApiCommonResponse.ok());
    }

    @DeleteMapping("/me/blocks/{blockId}")
    public ResponseEntity<ApiCommonResponse<Void>> unblockOther(@PathVariable Long blockId)  {
        crewBlockService.removeBlock(blockId);
        return ResponseEntity.ok()
                .body(ApiCommonResponse.ok());
    }
}
