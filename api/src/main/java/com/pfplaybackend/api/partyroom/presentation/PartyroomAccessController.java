package com.pfplaybackend.api.partyroom.presentation;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Partyroom API")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class PartyroomAccessController {

    @PostMapping("/{partyroomId}/enter")
    public ResponseEntity<Void> enterPartyroom(
            @PathVariable Long partyroomId) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{partyroomId}/exit")
    public ResponseEntity<Void> exitPartyroom(
            @PathVariable Long partyroomId) {
        return ResponseEntity.ok().build();
    }
}
