package com.crm.gym.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.Objects;

@Configuration
public class OpenAPIConfig
{
    @Bean
    public OpenAPI openAPI()
    {
        return new OpenAPI()
                .info(new Info()
                    .title("Gym Reports Microservice RESTful API")
                    .version("v1.0")
                    .description("RESTful API microservice that complements the gym CRM solution, focused on managing trainers' workloads.")
                    .contact(
                            new Contact()
                            .name("Levir Hernandez")
                            .url("https://www.linkedin.com/in/levir-heladio-hernandez-suarez-6916a6254")
                    )
                )
                .components(new Components()
                        .addSecuritySchemes(
                                "user_auth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("After obtaining a token, provide it here to authorize your requests.")
                        )
                );
    }

    @Bean
    public GlobalOpenApiCustomizer globalOpenApiSecurityResponsesCustomizer()
    {
        return openApi -> openApi.getPaths().values()
                .forEach(path -> path.readOperations().stream()
                        .filter(operation ->
                                // Filter only secured operations
                                Objects.nonNull(operation.getSecurity())
                        )
                        .forEach(operation ->
                                // Add 401 Unauthorized response
                                operation.getResponses()
                                        .addApiResponse(
                                            String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                                            new ApiResponse().description("Bearer Authentication is required to access this resource")
                                        )
                                        .addApiResponse(
                                                String.valueOf(HttpStatus.FORBIDDEN.value()),
                                                new ApiResponse().description("Token is invalid or lacks the appropriate role")
                                        )
                        )
        );
    }
}
