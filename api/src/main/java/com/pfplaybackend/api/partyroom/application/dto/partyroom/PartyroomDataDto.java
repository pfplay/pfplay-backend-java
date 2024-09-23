package com.pfplaybackend.api.partyroom.application.dto.partyroom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PartyroomDataDto {
    private Long id;
    private String title;
    private String introduction;
    private Set<CrewDataDto> crewDataSet;
    private Set<DjDataDto> djDataSet;
}
