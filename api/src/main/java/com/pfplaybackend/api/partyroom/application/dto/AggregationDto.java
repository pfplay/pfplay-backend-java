package com.pfplaybackend.api.partyroom.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AggregationDto {
    private int likeCount;
    private int dislikeCount;
    private int grabCount;
}