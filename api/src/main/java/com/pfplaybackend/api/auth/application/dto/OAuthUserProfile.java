package com.pfplaybackend.api.auth.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OAuthUserProfile {
    private String id;
    private String email;
    private String name;
    private String picture;
}
