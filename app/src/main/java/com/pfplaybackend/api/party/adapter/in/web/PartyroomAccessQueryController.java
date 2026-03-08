package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.config.swagger.ApiErrorCodes;
import com.pfplaybackend.api.party.application.dto.partyroom.LinkEnterDto;
import com.pfplaybackend.api.party.application.service.PartyroomAccessQueryService;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
public class PartyroomAccessQueryController {

    private final PartyroomAccessQueryService partyroomAccessQueryService;

    @Operation(summary = "링크로 파티룸 정보 조회", description = "공유 링크 도메인으로 파티룸 정보를 조회합니다. (permitAll)")
    @ApiErrorCodes({PartyroomException.class})
    @GetMapping("/link/{linkDomain}")
    public ResponseEntity<ApiCommonResponse<LinkEnterDto>> getPartyroomByLink(
            @Parameter(description = "파티룸 공유 링크 도메인") @PathVariable String linkDomain) {
        return ResponseEntity.ok().body(ApiCommonResponse.success(partyroomAccessQueryService.getPartyroomByLink(linkDomain)));
    }
}
