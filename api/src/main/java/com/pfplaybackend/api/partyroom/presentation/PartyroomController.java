package com.pfplaybackend.api.partyroom.presentation;

import com.pfplaybackend.api.partyroom.application.PartyroomService;
import com.pfplaybackend.api.partyroom.presentation.api.PartyroomApi;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/partyroom")
public class PartyroomController implements PartyroomApi {
    private final PartyroomService partyroomService;
}
