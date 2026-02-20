package com.pfplaybackend.api.common.domain.value;

import com.pfplaybackend.api.common.util.TsidGenerator;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
public class UserId implements Serializable {

    private Long uid;

    public UserId() {
        this.uid = TsidGenerator.nextId();
    }

    public UserId(Long uid) {
        this.uid = uid;
    }

    public static UserId create(Long uid) {
        return new UserId(uid);
    }

    public static UserId fromString(String uid) {
        return new UserId(Long.parseLong(uid));
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

    @Override
    public String toString() {
        return String.valueOf(uid);
    }
}
