package com.pfplaybackend.api.auth.domain.value;

public class OAuth2Redirection {
    final private String callbackUri;
    final private String redirectLocation;

    public OAuth2Redirection(String callbackUri, String redirectLocation) {
        this.callbackUri = callbackUri;
        this.redirectLocation = redirectLocation;
    }

    public String getUrl() {
        return "redirect:"
                + callbackUri
                + "?redirect_location="
                + this.redirectLocation;
    }

    static public OAuth2Redirection create(String callbackUri, String redirectLocation) {
        return new OAuth2Redirection(callbackUri, redirectLocation);
    }
}
