package com.pfplaybackend.api.partyroom.domain.model.value;

import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor
public class PartyroomId {
    private Long id;

    public PartyroomId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartyroomId partyroomId = (PartyroomId) o;
        return Objects.equals(id, partyroomId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
