package com.pfplaybackend.api.partyroom.domain.value;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
public class DjId implements Serializable {
    private long id;
    public DjId(long id) {
        this.id = id;
    }
    public DjId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DjId djId = (DjId) o;
        return id == djId.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
