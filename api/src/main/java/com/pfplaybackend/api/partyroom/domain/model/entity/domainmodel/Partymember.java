package com.pfplaybackend.api.partyroom.domain.model.entity.domainmodel;

import com.pfplaybackend.api.partyroom.domain.model.value.PartymemberId;
import lombok.Getter;

@Getter
public class Partymember {
    private PartymemberId partymemberId;

    public static Partymember create() {
        return new Partymember();
    }
}
