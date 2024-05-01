package com.pfplaybackend.api.user.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.BindParam;
import org.springframework.web.bind.annotation.RequestParam;

@Getter
@Setter
public class MemberSignRequest {

    @NotNull(message = "Name cannot be null")
    private final String oauth2Provider;
    @NotNull(message = "Name cannot be null")
    private final String redirectLocation;

    MemberSignRequest(@BindParam("oauth2_provider") String oauth2Provider,
                      @BindParam("redirect_location") String redirectLocation) {
        this.oauth2Provider = oauth2Provider;
        this.redirectLocation = redirectLocation;
    }
}
