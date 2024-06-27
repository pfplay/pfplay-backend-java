package com.pfplaybackend.api.partyroom.domain.value;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
public class PlaylistId implements Serializable {
    private long id;
    public PlaylistId() {}
    public PlaylistId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaylistId playlistId = (PlaylistId) o;
        return id == playlistId.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
