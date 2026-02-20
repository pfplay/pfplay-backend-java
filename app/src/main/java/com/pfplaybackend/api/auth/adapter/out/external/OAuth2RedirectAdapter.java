package com.pfplaybackend.api.auth.adapter.out.external;

import com.pfplaybackend.api.auth.adapter.out.external.config.OAuth2ProviderConfig;
import com.pfplaybackend.api.auth.domain.value.OAuth2Redirection;
import com.pfplaybackend.api.common.config.security.enums.ProviderType;
import com.pfplaybackend.api.user.application.port.out.OAuth2RedirectPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2RedirectAdapter implements OAuth2RedirectPort {

    private final OAuth2ProviderConfig oauth2ProviderConfig;

    @Override
    public String getRedirectUri(String oauth2Provider, String redirectLocation) {
        ProviderType.valueOf(oauth2Provider.toUpperCase());
        String callbackUri = oauth2ProviderConfig.getProviders().get(oauth2Provider.toLowerCase()).getUri();
        OAuth2Redirection oauth2Redirection = OAuth2Redirection.create(callbackUri, redirectLocation);
        return oauth2Redirection.getUrl();
    }
}
