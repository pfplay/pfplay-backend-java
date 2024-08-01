package com.pfplaybackend.api.user.domain.value;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class AvatarIconUri {
    private String avatarIconUri;

    public AvatarIconUri() {
        this.avatarIconUri = "";
    }
    public AvatarIconUri(String avatarIconUri) {
        this.avatarIconUri = avatarIconUri;
    }
}
