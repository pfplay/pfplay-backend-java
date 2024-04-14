package com.pfplaybackend.api.config.oauth2.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class Oauth2UserInfoDto {
    String email;
}
