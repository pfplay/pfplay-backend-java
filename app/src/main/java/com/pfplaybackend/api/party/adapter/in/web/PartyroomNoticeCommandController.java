package com.pfplaybackend.api.party.adapter.in.web;

import com.pfplaybackend.api.party.adapter.in.web.payload.request.management.UpdateNoticeRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Partyroom API")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class PartyroomNoticeCommandController {

    @PutMapping("/{partyroomId}/notice")
    public ResponseEntity<Void> registerNotice(@PathVariable Long partyroomId,
                                               UpdateNoticeRequest updateNoticeRequest) {
        return ResponseEntity.ok().build();
    }
}
