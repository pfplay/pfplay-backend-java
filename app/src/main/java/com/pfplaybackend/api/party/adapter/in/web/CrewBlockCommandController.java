package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.AddBlockRequest;
import com.pfplaybackend.api.party.application.dto.command.AddBlockCommand;
import com.pfplaybackend.api.party.application.service.CrewBlockCommandService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Crew API")
@RequestMapping("/api/v1/crews")
@RestController
@RequiredArgsConstructor
public class CrewBlockCommandController {

    private final CrewBlockCommandService crewBlockCommandService;

    @PostMapping("/me/blocks")
    public ResponseEntity<ApiCommonResponse<Void>> blockOtherCrew(@RequestBody AddBlockRequest request)  {
        crewBlockCommandService.addBlock(new AddBlockCommand(request.getCrewId()));
        return ResponseEntity.ok()
                .body(ApiCommonResponse.ok());
    }

    @DeleteMapping("/me/blocks/{blockId}")
    public ResponseEntity<ApiCommonResponse<Void>> unblockOther(@PathVariable Long blockId)  {
        crewBlockCommandService.removeBlock(blockId);
        return ResponseEntity.ok()
                .body(ApiCommonResponse.ok());
    }
}
