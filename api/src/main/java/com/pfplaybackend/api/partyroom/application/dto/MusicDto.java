package com.pfplaybackend.api.partyroom.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MusicDto {
    private String linkId;
    private String name;
    private String thumbnailImage;
    private String duration;
    private int orderNumber;
}
