package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.common.ApiCommonResponse;
import com.pfplaybackend.api.partyroom.application.service.PartyroomNoticeService;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.presentation.payload.request.UpdateNoticeRequest;
import com.pfplaybackend.api.partyroom.presentation.payload.response.QueryPartyroomNoticeResponse;
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
    public ResponseEntity<?> getNotice(@PathVariable Long partyroomId) {
        String content = partyroomNoticeService.getNotice(new PartyroomId(partyroomId));
        return ResponseEntity.ok().body(ApiCommonResponse.success(new QueryPartyroomNoticeResponse(content)));
    }
}
