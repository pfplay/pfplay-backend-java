package com.pfplaybackend.api.auth.adapter.out.external.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.webclient")
public class WebClientProperties {

    private TimeoutProperties timeout = new TimeoutProperties();
    private Integer maxInMemorySize = 1048576; // 1MB

    @Data
    public static class TimeoutProperties {
        private Integer connectionMs = 5000;
        private Integer readMs = 5000;
        private Integer writeMs = 5000;
    }
}
