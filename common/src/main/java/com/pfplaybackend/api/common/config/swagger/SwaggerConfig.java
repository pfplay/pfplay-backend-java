package com.pfplaybackend.api.common.config.swagger;

import com.pfplaybackend.api.common.ApiErrorResponse;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@SecurityScheme(
        name = "cookieAuth",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.COOKIE,
        paramName = "accessToken"
)
@Configuration
public class SwaggerConfig {

    @Value("${springdoc.version}")
    private String version;
    private static final String LOCAL_URL = "http://localhost:8080";
    private static final String PROD_URL = "https://pfplay-api.app";

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

    @Bean
    public GroupedOpenApi apiGroup(ApiErrorCodeCustomizer apiErrorCodeCustomizer) {
        return GroupedOpenApi.builder()
                .group("api")
                .pathsToMatch("/api/**")
                .addOperationCustomizer(apiErrorCodeCustomizer)
                .addOpenApiCustomizer(apiErrorResponseSchemaCustomizer())
                .build();
    }

    @Bean
    public OpenApiCustomizer apiErrorResponseSchemaCustomizer() {
        return openApi -> {
            Schema<ApiErrorResponse> errorSchema = new Schema<>();
            errorSchema.setType("object");
            errorSchema.addProperty("status", new Schema<Integer>().type("integer").description("HTTP 상태 코드"));
            errorSchema.addProperty("errorCode", new Schema<String>().type("string").description("도메인 에러 코드 (예: PTR-001, DJ-001)"));
            errorSchema.addProperty("message", new Schema<String>().type("string").description("에러 메시지"));
            errorSchema.setDescription("공통 에러 응답");
            openApi.getComponents().addSchemas("ApiErrorResponse", errorSchema);
        };
    }
}
