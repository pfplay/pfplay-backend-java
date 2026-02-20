package com.pfplaybackend.api.auth.adapter.out.external;

import com.pfplaybackend.api.auth.adapter.out.external.config.OAuth2ProviderConfig;
import com.pfplaybackend.api.auth.domain.value.OAuth2Redirection;
import com.pfplaybackend.api.user.application.port.out.OAuth2RedirectPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2RedirectAdapter implements OAuth2RedirectPort {

    private final OAuth2ProviderConfig oauth2ProviderConfig;

    @Override
    public String getRedirectUri(String oauth2Provider, String redirectLocation) {
        OAuth2Redirection oauth2Redirection = OAuth2Redirection.create(
                oauth2ProviderConfig.getProviders(), oauth2Provider, redirectLocation);
        return oauth2Redirection.getUrl();
    }
}
