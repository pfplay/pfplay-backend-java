package com.pfplaybackend.api.partyroom.presentation.dto;

import com.pfplaybackend.api.common.dto.PaginationDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PartyRoomHomeResultPaginationDto {
    private List<PartyRoomHomeResultDto> content;
    private PaginationDto pagination;
}

