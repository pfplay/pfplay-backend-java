package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.partyroom.application.service.SamplePartyroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/partyrooms")
@RestController
@RequiredArgsConstructor
public class SamplePartyroomController {

    private final SamplePartyroomService samplePartyroomService;

    @GetMapping("/a")
    public void method_a() {
        samplePartyroomService.method_a();
    }

    @GetMapping("/b")
    public void method_b() {
        // samplePartyroomService.method_b();
    }
}
