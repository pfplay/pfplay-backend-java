package com.pfplaybackend.api.party.domain.value;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
public class PartyroomId implements Serializable {
    private long id;
    public PartyroomId() {}
    @JsonCreator
    public PartyroomId(long id) {
        this.id = id;
    }

    @JsonValue
    public long value() {
        return id;
    }

    public static PartyroomId of(Long id) {
        return new PartyroomId(id);
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

