package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.partyroom.application.service.PartyroomAccessService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Partyroom Access API", description = "Operations related to partyroom management")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class PartyroomAccessController {

    private final PartyroomAccessService partyroomAccessService;

    @PostMapping("/{partyroomId}/enter")
    public ResponseEntity<Void> enterPartyroom(
            @PathVariable Long partyroomId) {
        partyroomAccessService.enter();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/link/{linkAddress}/enter")
    public ResponseEntity<Void> enterPartyroomByLinkAddress(
            @PathVariable String linkAddress) {
        partyroomAccessService.enter();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{partyroomId}/exit")
    public ResponseEntity<Void> exitPartyroom(
            @PathVariable Long partyroomId) {
        partyroomAccessService.exit();
        return ResponseEntity.ok().build();
    }
}
