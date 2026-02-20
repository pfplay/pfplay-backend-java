package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.party.application.service.PartyroomNoticeService;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.management.UpdateNoticeRequest;
import com.pfplaybackend.api.party.adapter.in.web.payload.response.info.QueryPartyroomNoticeResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Partyroom API")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class PartyroomNoticeController {

    final private PartyroomNoticeService partyroomNoticeService;

    @PutMapping("/{partyroomId}/notice")
    public ResponseEntity<Void> registerNotice(@PathVariable Long partyroomId,
                                               UpdateNoticeRequest updateNoticeRequest) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{partyroomId}/notice")
    public ResponseEntity<ApiCommonResponse<QueryPartyroomNoticeResponse>> getNotice(@PathVariable Long partyroomId) {
        String content = partyroomNoticeService.getNotice(new PartyroomId(partyroomId));
        return ResponseEntity.ok().body(ApiCommonResponse.success(new QueryPartyroomNoticeResponse(content)));
    }
}
