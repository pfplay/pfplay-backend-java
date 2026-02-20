package com.pfplaybackend.api.user.application.port.out;

public interface OAuth2RedirectPort {
    String getRedirectUri(String oauth2Provider, String redirectLocation);
}
