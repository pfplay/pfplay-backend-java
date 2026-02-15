package com.pfplaybackend.api.auth.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pfplaybackend.api.auth.dto.response.OAuthTokenResponse;
import com.pfplaybackend.api.auth.dto.OAuthUserProfile;
import com.pfplaybackend.api.common.config.security.jwt.properties.OAuth2Properties;
import com.pfplaybackend.api.common.exception.OAuthException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Base64;

@Slf4j
@Component
public class TwitterOAuthClient {

    private final WebClient webClient;
    private final OAuth2Properties.Provider twitterConfig;

    public TwitterOAuthClient(WebClient.Builder webClientBuilder, OAuth2Properties oAuth2Properties) {
        this.webClient = webClientBuilder.build();
        this.twitterConfig = oAuth2Properties.getProviders().get("twitter");
    }

    public OAuthTokenResponse exchangeCodeForToken(String code, String codeVerifier) {
        log.debug("Exchanging Twitter authorization code for token");

        String credentials = twitterConfig.getClientId() + ":" + twitterConfig.getClientSecret();
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", twitterConfig.getRedirectUri());
        formData.add("code_verifier", codeVerifier);

        return webClient.post()
                .uri(twitterConfig.getTokenUri())
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .map(error -> {
                                    log.error("Twitter token exchange failed: {}", error);
                                    return new OAuthException("Failed to exchange code for token: " + error);
                                })
                                .switchIfEmpty(Mono.error(new OAuthException("Failed to exchange code for token")))
                )
                .bodyToMono(OAuthTokenResponse.class)
                .doOnSuccess(response -> log.debug("Successfully obtained Twitter access token"))
                .doOnError(error -> log.error("Error exchanging Twitter code for token", error))
                .block();
    }

    public OAuthUserProfile getUserProfile(String accessToken) {
        log.debug("Fetching Twitter user profile");

        return webClient.get()
                .uri(twitterConfig.getUserInfoUri() + "?user.fields=id,name,username,profile_image_url")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .map(error -> {
                                    log.error("Failed to fetch Twitter user profile: {}", error);
                                    return new OAuthException("Failed to fetch user profile: " + error);
                                })
                                .switchIfEmpty(Mono.error(new OAuthException("Failed to fetch user profile")))
                )
                .bodyToMono(TwitterUserResponse.class)
                .map(this::mapToUserProfile)
                .doOnSuccess(profile -> log.debug("Successfully fetched Twitter user profile: {}", profile.getEmail()))
                .doOnError(error -> log.error("Error fetching Twitter user profile", error))
                .onErrorMap(WebClientResponseException.class, ex -> {
                    log.error("WebClient error - Status: {}, Body: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
                    return new OAuthException("Failed to fetch user profile from Twitter: " + ex.getMessage());
                })
                .block();
    }

    /**
     * Twitter 사용자 정보를 OAuthUserProfile로 변환
     * username을 이메일 대용으로 사용
     */
    private OAuthUserProfile mapToUserProfile(TwitterUserResponse response) {
        TwitterUserInfo user = response.getData();

        // username을 이메일 형식으로 변환
        String emailLikeUsername = generateEmailFromUsername(user.getUsername());

        log.debug("Twitter user mapping - ID: {}, Username: {}, Email-like: {}",
                user.getId(), user.getUsername(), emailLikeUsername);

        return OAuthUserProfile.builder()
                .id(user.getId())
                .email(emailLikeUsername)  // username을 이메일 대용으로 사용
                .name(user.getName() != null ? user.getName() : user.getUsername())
                .picture(user.getProfileImageUrl())
                .build();
    }

    /**
     * Twitter username으로부터 이메일 형식 문자열 생성
     *
     * @param username Twitter username (예: "john_doe")
     * @return 이메일 형식 문자열 (예: "john_doe@x.com")
     */
    private String generateEmailFromUsername(String username) {
        if (username == null || username.isBlank()) {
            log.warn("Twitter username is null or empty, using fallback");
            return "unknown@x.com";
        }

        // @ 기호가 이미 있으면 그대로 사용 (혹시 Twitter에서 이메일을 제공하는 경우)
        if (username.contains("@")) {
            return username;
        }

        // username에서 특수문자 정리 (이메일 형식에 맞게)
        String cleanUsername = username.replaceAll("[^a-zA-Z0-9._-]", "");

        // 최소 길이 보장
        if (cleanUsername.length() < 3) {
            cleanUsername = "user" + cleanUsername;
        }

        return cleanUsername + "@x.com";
    }

    @Data
    private static class TwitterUserResponse {
        private TwitterUserInfo data;
    }

    @Data
    private static class TwitterUserInfo {
        private String id;
        private String name;
        private String username;

        @JsonProperty("profile_image_url")
        private String profileImageUrl;
    }
}