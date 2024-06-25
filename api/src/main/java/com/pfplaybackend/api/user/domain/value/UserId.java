package com.pfplaybackend.api.user.domain.value;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Embeddable
public class UserId {

    private UUID uid;

    public UserId() {
        this.uid = UUID.randomUUID();
    }

    public UserId(UUID uid) {
        this.uid = uid;
    }

    public static UserId create(UUID uid) {
        return new UserId(uid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId userId = (UserId) o;
        return uid.equals(userId.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }
}
