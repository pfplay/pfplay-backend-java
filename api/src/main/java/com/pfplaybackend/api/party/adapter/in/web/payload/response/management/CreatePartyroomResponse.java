package com.pfplaybackend.api.party.adapter.in.web.payload.response.management;

import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
@Data
public class CreatePartyroomResponse {
    private long partyroomId;

    public static CreatePartyroomResponse from(PartyroomData partyroom) {
        return CreatePartyroomResponse.builder()
                .partyroomId(partyroom.getPartyroomId().getId())
                .build();
    }
}
