package com.pfplaybackend.api.config.swagger;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

//@SecurityScheme(
//    name = "Bearer Authentication",
//    type = SecuritySchemeType.HTTP,
//    bearerFormat = "JWT",
//    in = SecuritySchemeIn.HEADER,
//    scheme = "bearer"
//)
@Configuration
public class SwaggerConfig {

    @Value("${springdoc.version}")
    private String version;
    private final String LOCAL_URL = "http://localhost:8080";
    private final String PROD_URL = "https://pfplay-api.app";

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .version(version)
                .title("pfplay API")
                .description("pfplay backend");

        Server localServer = new Server();
        localServer.setDescription("backend local");
        localServer.setUrl(LOCAL_URL);

        Server prodServer = new Server();
        prodServer.setDescription("prod");
        prodServer.setUrl(PROD_URL);

        return new OpenAPI()
                .servers(Arrays.asList(prodServer, localServer))
                .info(info);
    }
}
