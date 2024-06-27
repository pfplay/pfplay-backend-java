package com.pfplaybackend.api.partyroom.domain.value;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
public class PartymemberId implements Serializable {
    private long id;
    public PartymemberId() {}
    public PartymemberId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartymemberId partymemberId = (PartymemberId) o;
        return id == partymemberId.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
