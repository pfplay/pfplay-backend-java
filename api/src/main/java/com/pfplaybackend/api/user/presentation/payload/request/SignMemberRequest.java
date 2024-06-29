package com.pfplaybackend.api.user.presentation.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.BindParam;

@Getter
@Setter
public class SignMemberRequest {

    @NotNull(message = "Name cannot be null")
    private final String oauth2Provider;

    SignMemberRequest(@BindParam("oauth2Provider") String oauth2Provider) {
        this.oauth2Provider = oauth2Provider;
    }
}