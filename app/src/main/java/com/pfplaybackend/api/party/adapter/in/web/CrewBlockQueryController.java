package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.party.application.dto.result.BlockedCrewResult;
import com.pfplaybackend.api.party.application.service.CrewBlockQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Crew API")
@RequestMapping("/api/v1/crews")
@RestController
@RequiredArgsConstructor
public class CrewBlockQueryController {

    private final CrewBlockQueryService crewBlockQueryService;

    @GetMapping("/me/blocks")
    public ResponseEntity<ApiCommonResponse<List<BlockedCrewResult>>> getBlockCrews()  {
        return ResponseEntity.ok()
                .body(ApiCommonResponse.success(crewBlockQueryService.getBlocks()));
    }
}
