package com.pfplaybackend.api.partyroom.domain.value;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
public class PartyroomId {
    private Long id;

    public PartyroomId() {}

    public PartyroomId(Long id) {
        this.id = id;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartyroomId partyroomId = (PartyroomId) o;
        return Objects.equals(id, partyroomId.id);
    }

    public int hashCode() {
        return Objects.hash(id);
    }

    public String toString() {
        return String.valueOf(id);
    }
}
