package com.pfplaybackend.api.partyroom.domain.value;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
public class PlaybackId implements Serializable {
    long id;
    public PlaybackId() {}
    public PlaybackId(long id) {this.id = id;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaybackId playbackId = (PlaybackId) o;
        return id == playbackId.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}