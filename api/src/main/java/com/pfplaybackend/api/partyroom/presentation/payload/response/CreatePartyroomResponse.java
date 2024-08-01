package com.pfplaybackend.api.partyroom.presentation.payload.response;

import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
@Data
public class CreatePartyroomResponse {
    private long partyroomId;

    public static CreatePartyroomResponse from(Partyroom partyroom) {
        return CreatePartyroomResponse.builder()
                .partyroomId(partyroom.getPartyroomId().getId())
                .build();
    }
}
