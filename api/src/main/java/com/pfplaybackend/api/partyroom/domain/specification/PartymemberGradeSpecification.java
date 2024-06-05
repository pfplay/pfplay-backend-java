package com.pfplaybackend.api.partyroom.domain.specification;

import com.pfplaybackend.api.partyroom.domain.model.collection.Partymember;

public class PartymemberGradeSpecification {
    public boolean isAllowedToUpdateLevel(Partymember partymember) {
        return false;
    }

    public boolean isAllowedToImpose(Partymember partymember) {
        return false;
    }
}
