package com.pfplaybackend.api.partyroom.domain.value;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
public class CrewId implements Serializable {
    private long id;
    public CrewId() {}
    public CrewId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CrewId crewId = (CrewId) o;
        return id == crewId.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
