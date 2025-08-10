package com.pfplaybackend.api.common.config.security.model;

import com.pfplaybackend.api.common.config.security.properties.OAuth2ProviderConfig;
import com.pfplaybackend.api.common.config.security.enums.ProviderType;

import java.util.Map;

public class OAuth2Redirection {
    final private Map<String, OAuth2ProviderConfig.Environment> providers;
    final private String oauth2Provider;
    final private String redirectLocation;

    public OAuth2Redirection(Map<String, OAuth2ProviderConfig.Environment> providers, String oauth2Provider, String redirectLocation) {
        try {
            ProviderType.valueOf(oauth2Provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw e;
        }
        this.providers = providers;
        this.oauth2Provider = oauth2Provider;
        this.redirectLocation = redirectLocation;
    }

    public String getUrl() {
        String callbackUri = this.providers.get(this.oauth2Provider.toLowerCase()).getUri();
        return "redirect:"
                + callbackUri
                + "?redirect_location="
                + this.redirectLocation;
    }

    static public OAuth2Redirection create(Map<String, OAuth2ProviderConfig.Environment> providers, String oauth2Provider, String redirectLocation) {
        return new OAuth2Redirection(providers, oauth2Provider, redirectLocation);
    }
}