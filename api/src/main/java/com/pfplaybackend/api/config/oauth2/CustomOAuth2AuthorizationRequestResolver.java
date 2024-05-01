package com.pfplaybackend.api.config.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

@RequiredArgsConstructor
public class CustomOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private OAuth2AuthorizationRequestResolver defaultResolver;

    public CustomOAuth2AuthorizationRequestResolver(
            ClientRegistrationRepository repo) {
        defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization/");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest = this.defaultResolver.resolve(request);
        if(hasQueryString(request)) {
            return customizeAuthorizationRequestWithRedirectLocation(request, authorizationRequest);
        }
        return customizeAuthorizationRequest(authorizationRequest);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = this.defaultResolver.resolve(request, clientRegistrationId);
        return customizeAuthorizationRequest(authorizationRequest);
    }

    private OAuth2AuthorizationRequest customizeAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest) {
        if (authorizationRequest == null) {
            return null;
        }
        OAuth2AuthorizationRequest.Builder builder = OAuth2AuthorizationRequest.from(authorizationRequest);
        return builder.build();
    }

    private OAuth2AuthorizationRequest customizeAuthorizationRequestWithRedirectLocation(HttpServletRequest request, OAuth2AuthorizationRequest authorizationRequest) {
        if (authorizationRequest == null) {
            return null;
        }
        OAuth2AuthorizationRequest.Builder builder = OAuth2AuthorizationRequest.from(authorizationRequest);
        if(hasRedirectLocation(request)) {
            String redirectLocation = getRedirectLocation(request);
            builder.state(redirectLocation);
        }
        return builder.build();
    }

    private boolean hasRedirectLocation(HttpServletRequest request) {
        return request.getQueryString().split("=")[0].equals("redirect_location");
    }

    private String getRedirectLocation(HttpServletRequest request) {
        return request.getQueryString().split("=")[1];
    }

    private boolean hasQueryString(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return queryString != null;
    }
}
