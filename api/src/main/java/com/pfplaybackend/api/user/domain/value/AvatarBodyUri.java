package com.pfplaybackend.api.user.domain.value;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class AvatarBodyUri {

    private String avatarBodyUri;

    public AvatarBodyUri() {
        this.avatarBodyUri = "";
    }
    public AvatarBodyUri(String avatarBodyUri) {
        this.avatarBodyUri = avatarBodyUri;
    }
}
