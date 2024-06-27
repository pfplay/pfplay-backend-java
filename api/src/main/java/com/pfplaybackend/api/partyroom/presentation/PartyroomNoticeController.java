package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.partyroom.application.service.PartyroomNoticeService;
import com.pfplaybackend.api.partyroom.presentation.payload.request.UpdateNoticeRequest;
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
}
