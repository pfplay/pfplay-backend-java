package com.pfplaybackend.api.partyroom.presentation.payload.response;

import com.pfplaybackend.api.partyroom.application.dto.DisplayDto;
import com.pfplaybackend.api.partyroom.application.dto.PartymemberDto;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Builder
@Data
public class QueryDisplayInfoResponse {
    List<PartymemberDto> members;
    DisplayDto display;
}
