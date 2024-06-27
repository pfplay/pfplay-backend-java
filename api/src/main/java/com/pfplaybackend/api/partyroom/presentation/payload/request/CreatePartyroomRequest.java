package com.pfplaybackend.api.partyroom.presentation.payload.request;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class CreatePartyroomRequest {
    private String title;
    private String introduction;
    private String linkDomain;
    private int playbackTimeLimit;
}
