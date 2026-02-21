package com.pfplaybackend.api.auth.adapter.out.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pfplaybackend.api.auth.application.dto.oauth.OAuthTokenDto;
import com.pfplaybackend.api.auth.application.dto.oauth.OAuthUserProfileDto;
import com.pfplaybackend.api.auth.adapter.out.external.config.OAuth2Properties;
import com.pfplaybackend.api.common.exception.OAuthException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GoogleOAuthClient {

    private final WebClient webClient;
    private final OAuth2Properties.Provider googleConfig;

    public GoogleOAuthClient(WebClient.Builder webClientBuilder, OAuth2Properties oAuth2Properties) {
        this.webClient = webClientBuilder.build();
        this.googleConfig = oAuth2Properties.getProviders().get("google");
    }

    public OAuthTokenDto exchangeCodeForToken(String code, String codeVerifier) {
        log.debug("Exchanging Google OAuth code for token");

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("client_id", googleConfig.getClientId());
        formData.add("client_secret", googleConfig.getClientSecret());
        formData.add("redirect_uri", googleConfig.getRedirectUri());
        formData.add("grant_type", "authorization_code");
        formData.add("code_verifier", codeVerifier);

        return webClient.post()
                .uri(googleConfig.getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .map(error -> {
                                    log.error("Google token exchange failed: {}", error);
                                    return new OAuthException("Failed to exchange code for token: " + error);
                                })
                                .switchIfEmpty(Mono.error(new OAuthException("Failed to exchange code for token")))
                )
                .bodyToMono(OAuthTokenDto.class)
                .doOnSuccess(response -> log.debug("Successfully obtained Google access token"))
                .doOnError(error -> log.error("Error exchanging Google code for token", error))
                .block();
    }

    public OAuthUserProfileDto getUserProfile(String accessToken) {
        log.debug("Fetching Google user profile");

        return webClient.get()
                .uri(googleConfig.getUserInfoUri())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .map(error -> {
                                    log.error("Google token exchange failed: {}", error);
                                    return new OAuthException("Failed to exchange code for token: " + error);
                                })
                                .switchIfEmpty(Mono.error(new OAuthException("Failed to exchange code for token")))
                )
                .bodyToMono(GoogleUserInfo.class)
                .map(this::mapToUserProfile)
                .doOnSuccess(profile -> log.debug("Successfully fetched Google user profile: {}", profile.email()))
                .doOnError(error -> log.error("Error fetching Google user profile", error))
                .block();
    }

    private OAuthUserProfileDto mapToUserProfile(GoogleUserInfo googleUser) {
        return new OAuthUserProfileDto(
                googleUser.getId(),
                googleUser.getEmail(),
                googleUser.getName(),
                googleUser.getPicture()
        );
    }

    @Data
    private static class GoogleUserInfo {
        private String id;
        private String email;
        private String name;
        private String picture;

        @JsonProperty("verified_email")
        private Boolean verifiedEmail;
    }
}
