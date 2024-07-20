package com.pfplaybackend.api.partyroom.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AggregationDto {
    private int likeCount;
    private int dislikeCount;
    private int grabCount;
}
