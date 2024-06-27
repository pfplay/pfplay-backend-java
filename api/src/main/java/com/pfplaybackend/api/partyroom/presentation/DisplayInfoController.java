package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.partyroom.application.service.DisplayInfoService;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "DJ API")
@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class DisplayInfoController {

    private final DisplayInfoService displayInfoService;

    @GetMapping("/{partyroomId}/display")
    public void getDisplayInfo(@PathVariable Long partyroomId) {
        displayInfoService.getDisplayInfo();
    }
}
