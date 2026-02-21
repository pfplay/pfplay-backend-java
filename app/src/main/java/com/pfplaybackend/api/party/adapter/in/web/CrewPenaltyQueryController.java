package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.party.application.dto.result.PenaltyResult;
import com.pfplaybackend.api.party.application.service.CrewPenaltyQueryService;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Partyroom API")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class CrewPenaltyQueryController {

    final private CrewPenaltyQueryService crewPenaltyQueryService;

    @GetMapping("/{partyroomId}/penalties")
    public ResponseEntity<ApiCommonResponse<List<PenaltyResult>>> getAllPenalties(@PathVariable("partyroomId") Long id) {
        return ResponseEntity.ok(
                ApiCommonResponse.success(crewPenaltyQueryService.getPenalties(PartyroomId.of(id))));
    }
}
