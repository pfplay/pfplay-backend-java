package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.common.config.swagger.ApiErrorCodes;
import com.pfplaybackend.api.party.adapter.in.web.payload.response.info.QueryPartyroomNoticeResponse;
import com.pfplaybackend.api.party.application.service.PartyroomNoticeQueryService;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
public class PartyroomNoticeQueryController {

    final private PartyroomNoticeQueryService partyroomNoticeQueryService;

    @Operation(summary = "공지사항 조회", description = "파티룸의 현재 공지사항을 조회합니다.")
    @SecurityRequirement(name = "cookieAuth")
    @ApiErrorCodes({PartyroomException.class})
    @GetMapping("/{partyroomId}/notice")
    public ResponseEntity<ApiCommonResponse<QueryPartyroomNoticeResponse>> getNotice(
            @Parameter(description = "파티룸 ID") @PathVariable Long partyroomId) {
        String content = partyroomNoticeQueryService.getNotice(new PartyroomId(partyroomId));
        return ResponseEntity.ok().body(ApiCommonResponse.success(new QueryPartyroomNoticeResponse(content)));
    }
}
