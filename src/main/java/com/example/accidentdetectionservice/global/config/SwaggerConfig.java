package com.example.accidentdetectionservice.global.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@OpenAPIDefinition(
        info = @Info(
                title = "Accident Detection",
                description = "Live Process",
                version = "v1"
        )
)
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(){
        String key = "Access Token (Bearer)";
        String refreshKey = "Refresh Token";

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(key)
                .addList(refreshKey);

        SecurityScheme accessTokenSecurityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer") //
                .bearerFormat("JWT")
                .in(SecurityScheme.In.COOKIE)
                .name("Authorization"); // Access Token

        SecurityScheme refreshTokenSecurityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE) // Refresh Token
                .name("Refresh");

        Components components = new Components()
                .addSecuritySchemes(key, accessTokenSecurityScheme)
                .addSecuritySchemes(refreshKey, refreshTokenSecurityScheme);

        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info())
                .addSecurityItem(securityRequirement)
                .components(components);
    }

}
