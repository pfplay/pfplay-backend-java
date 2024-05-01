package com.pfplaybackend.api.config.oauth2.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Component;

import java.util.Map;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "app.security.oauth2")
@ConfigurationPropertiesScan
public class OAuth2ProviderConfig {
    private Map<String, Environment> providers;

    @Setter
    @Getter
    public static class Environment {
        private String uri;
    }
}