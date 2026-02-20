package com.pfplaybackend.api.profile.domain.value;

import java.io.Serializable;

public record Nickname(String value) implements Serializable {

    public Nickname {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Nickname cannot be blank");
        }
        if (value.length() > 20) {
            throw new IllegalArgumentException("Nickname must be 20 characters or less");
        }
    }
}
