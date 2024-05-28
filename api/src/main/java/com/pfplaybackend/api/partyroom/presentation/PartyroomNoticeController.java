package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.partyroom.application.service.PartyroomNoticeService;
import com.pfplaybackend.api.partyroom.presentation.payload.request.RegisterNoticeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/partyrooms")
@RequiredArgsConstructor
public class PartyroomNoticeController {

    final private PartyroomNoticeService partyroomNoticeService;

    @PutMapping("/{partyroomId}/notice")
    public ResponseEntity<Void> registerNotice(@PathVariable Long partyroomId,
                                               RegisterNoticeRequest registerNoticeRequest) {
        return ResponseEntity.ok().build();
    }
}
