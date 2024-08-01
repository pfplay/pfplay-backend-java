package com.pfplaybackend.api.partyroom.presentation.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class CreatePartyroomRequest {
    private String title;
    private String introduction;
    private String linkDomain;
    private int playbackTimeLimit;
}