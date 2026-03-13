package com.edumora.trayectoria.infrastructure.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun openApi(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("TrayectorIA API")
                .description(
                    """
                    ## AI-Powered Job Networking Platform
                    
                    Connects candidates with companies. The AI generates personalized resumes 
                    and simulates interviews tailored to each job listing.
                    
                    ### Authentication
                    Use **POST /v1/auth/login** to obtain a JWT token.
                    Then click **Authorize** (top right) and paste the token.
                    
                    ### Roles
                    - `ROLE_CANDIDATE` — job seekers looking for opportunities
                    - `ROLE_COMPANY` — companies posting job offers
                    """.trimIndent()
                )
                .version("1.0.0")
                .contact(
                    Contact()
                        .name("Leandro Mora Corrales")
                        .email("moracorralesleandro@gmail.com")
                        .url("https://linkedin.com/in/leandromora")
                )
        )
        .addSecurityItem(SecurityRequirement().addList("bearerAuth"))
        .components(
            Components().addSecuritySchemes(
                "bearerAuth",
                SecurityScheme()
                    .name("bearerAuth")
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT token obtained from the login endpoint")
            )
        )
}