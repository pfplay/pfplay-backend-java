package com.pfplaybackend.api.partyview.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.partyview.adapter.in.web.payload.response.QueryPartyroomSetupResponse;
import com.pfplaybackend.api.partyview.application.dto.result.PartyroomSetupResult;
import com.pfplaybackend.api.partyview.application.service.PartyroomSetupQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Partyroom API")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class PartyroomSetupController {

    private final PartyroomSetupQueryService partyroomSetupQueryService;

    @GetMapping("/{partyroomId}/setup")
    public ResponseEntity<ApiCommonResponse<QueryPartyroomSetupResponse>> getSetupInfo(@PathVariable Long partyroomId) {
        PartyroomSetupResult result = partyroomSetupQueryService.getSetupInfo(new PartyroomId(partyroomId));
        return ResponseEntity.ok().body(
                ApiCommonResponse.success(QueryPartyroomSetupResponse.from(result.crews(), result.display())));
    }
}
