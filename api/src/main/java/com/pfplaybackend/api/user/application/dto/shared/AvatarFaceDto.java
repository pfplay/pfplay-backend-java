package com.pfplaybackend.api.user.application.dto.shared;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AvatarFaceDto {
    private final String name;
    private final String resourceUri;
}
