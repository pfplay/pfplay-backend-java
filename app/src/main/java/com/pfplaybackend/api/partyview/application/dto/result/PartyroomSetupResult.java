package com.pfplaybackend.api.partyview.application.dto.result;

import com.pfplaybackend.api.partyview.application.dto.CrewSetupDto;
import com.pfplaybackend.api.partyview.application.dto.DisplayDto;

import java.util.List;

public record PartyroomSetupResult(
    List<CrewSetupDto> crews,
    DisplayDto display
) {}
