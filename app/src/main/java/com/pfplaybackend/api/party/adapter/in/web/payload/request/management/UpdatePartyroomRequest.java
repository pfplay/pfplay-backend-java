package com.pfplaybackend.api.party.adapter.in.web.payload.request.management;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePartyroomRequest {
    private String title;
    private String introduction;
    private String linkDomain;
    private int playbackTimeLimit;
}
