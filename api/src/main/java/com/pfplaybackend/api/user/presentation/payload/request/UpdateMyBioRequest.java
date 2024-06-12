package com.pfplaybackend.api.user.presentation.payload.request;

import lombok.Getter;

@Getter
public class UpdateMyBioRequest {
    String nickname;
    String introduction;
}
