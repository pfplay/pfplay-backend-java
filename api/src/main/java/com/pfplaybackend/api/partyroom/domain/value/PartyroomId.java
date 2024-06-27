package com.pfplaybackend.api.partyroom.domain.value;


import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
public class PartyroomId implements Serializable {
    private long id;
    public PartyroomId() {}
    public PartyroomId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartyroomId partyroomId = (PartyroomId) o;
        return id == partyroomId.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

