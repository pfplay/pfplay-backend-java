package com.pfplaybackend.api.user.domain.value;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.ToString;

@Embeddable
@Getter
public class AvatarFaceUri {
    private String avatarFaceUri;

    public AvatarFaceUri() {
        this.avatarFaceUri = "";
    }

    public AvatarFaceUri(String avatarFaceUri) {
        this.avatarFaceUri = avatarFaceUri;
    }
}
