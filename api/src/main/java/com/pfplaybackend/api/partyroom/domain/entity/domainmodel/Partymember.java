package com.pfplaybackend.api.partyroom.domain.entity.domainmodel;

import com.pfplaybackend.api.partyroom.domain.value.PartymemberId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import lombok.Getter;

@Getter
public class Partymember {
    private PartymemberId partymemberId;

    public static Partymember create() {
        return new Partymember();
    }
}