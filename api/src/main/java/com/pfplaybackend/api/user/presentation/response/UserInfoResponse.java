package com.pfplaybackend.api.user.presentation.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserInfoResponse {

    private String id;
    private String email;
    private boolean verifiedEmail;
    private String picture;
    private String hd;

    @Builder
    public UserInfoResponse(String id, String email, boolean verifiedEmail, String picture, String hd) {
        this.id = id;
        this.email = email;
        this.verifiedEmail = verifiedEmail;
        this.picture = picture;
        this.hd = hd;
    }
}
