package com.pfplaybackend.api.user.adapter.in.web.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.BindParam;

@Getter
@Setter
public class MemberSignRequest {

    @NotNull(message = "Name cannot be null")
    private final String oauth2Provider;

    MemberSignRequest(@BindParam("oauth2Provider") String oauth2Provider) {
        this.oauth2Provider = oauth2Provider;
    }
}