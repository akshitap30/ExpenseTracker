package com.project1.ExpenseTracker.config;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI expenseTrackerAPI() {

        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()

                .info(

                        new Info()

                                .title("Expense Tracker API")

                                .description("""
                                    Secure Expense Tracker REST API

                                    Authentication:
                                    Register → Login → Copy JWT → Authorize
                                    """)

                                .version("1.0"))

                .addSecurityItem(

                        new SecurityRequirement()

                                .addList(securitySchemeName))

                .components(

                        new Components()

                                .addSecuritySchemes(

                                        securitySchemeName,

                                        new SecurityScheme()

                                                .name(securitySchemeName)

                                                .type(SecurityScheme.Type.HTTP)

                                                .scheme("bearer")

                                                .bearerFormat("JWT")));

    }

}