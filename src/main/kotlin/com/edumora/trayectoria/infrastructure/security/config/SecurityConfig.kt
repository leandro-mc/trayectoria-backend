package com.edumora.trayectoria.infrastructure.security.config

import com.edumora.trayectoria.infrastructure.security.jwt.JwtAuthenticationFilter
import com.edumora.trayectoria.infrastructure.security.service.UserDetailsServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 * @EnableWebSecurity     -> enables Spring Security's web security support
 * @EnableMethodSecurity  -> enables @PreAuthorize in controllers and use cases
 * E.g., @PreAuthorize("hasRole('CANDIDATE')")
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val userDetailsService: UserDetailsServiceImpl
) {

    /**
     * PasswordEncoder - BCrypt with strength 12 (default is 10).
     * BCrypt generates a random salt for each hash — two hashes of the
     * same password will never be identical.
     *
     * @Bean -> Spring manages this instance as a singleton.
     * It is automatically injected wherever a PasswordEncoder is required.
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    /**
     * DaoAuthenticationProvider - connects UserDetailsService with PasswordEncoder.
     * Spring uses it internally to validate credentials during login.
     */
    @Bean
    fun authenticationProvider(
        userDetailsService: UserDetailsService,
        passwordEncoder: PasswordEncoder
    ): DaoAuthenticationProvider =
        DaoAuthenticationProvider(userDetailsService).apply {
            setPasswordEncoder(passwordEncoder)
        }

    /**
     * AuthenticationManager - entry point for authenticating credentials.
     * We inject it into LoginUseCase to execute authentication
     * programmatically (without relying on the Spring filter).
     */
    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager

    /**
     * SecurityFilterChain - defines HTTP security rules.
     *
     * Order of rules: Spring evaluates from top to bottom
     * and applies the FIRST match. More specific rules must come first.
     */
    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        authenticationProvider: DaoAuthenticationProvider
    ): SecurityFilterChain {
        http
            // CSRF deshabilitado - no necesario en APIs REST stateless con JWT
            // CSRF protege forms de sesión, no tokens en headers
            .csrf { it.disable() }

            .cors { it.configurationSource(corsConfigurationSource()) }

            .authorizeHttpRequests { auth ->
                auth
                    //  Rutas públicas 
                    .requestMatchers(HttpMethod.POST, "/v1/auth/login").permitAll()
                    .requestMatchers(HttpMethod.POST, "/v1/auth/register/candidate").permitAll()
                    .requestMatchers(HttpMethod.POST, "/v1/auth/register/company").permitAll()
                    .requestMatchers(HttpMethod.POST, "/v1/auth/refresh").permitAll()

                    // Ofertas públicas - cualquiera puede ver ofertas sin autenticarse
                    .requestMatchers(HttpMethod.GET, "/v1/job-offers").permitAll()
                    .requestMatchers(HttpMethod.GET, "/v1/job-offers/{id}").permitAll()

                    // Skills catálogo - público
                    .requestMatchers(HttpMethod.GET, "/v1/skills").permitAll()

                    //  Rutas por rol 
                    .requestMatchers("/v1/candidates/**").hasRole("CANDIDATE")
                    .requestMatchers("/v1/companies/**").hasRole("COMPANY")
                    .requestMatchers("/v1/applications/**").hasAnyRole("CANDIDATE", "COMPANY")
                    .requestMatchers("/v1/saved-offers/**").hasRole("CANDIDATE")
                    .requestMatchers("/v1/curricula/{id}").hasAnyRole("CANDIDATE", "COMPANY")
                    .requestMatchers("/v1/curricula/latest").hasAnyRole("CANDIDATE", "COMPANY")
                    .requestMatchers("/v1/curricula/**").hasRole("CANDIDATE")
                    .requestMatchers("/v1/interviews/**").hasRole("CANDIDATE")

                    // Cualquier otra ruta requiere autenticación
                    .anyRequest().authenticated()
            }

            // STATELESS - sin HttpSession. Cada request es independiente.
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

            .authenticationProvider(authenticationProvider)

            // Nuestro filtro JWT va ANTES del filtro estándar de Spring
            // para que el SecurityContext esté cargado cuando Spring evalúe las reglas
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    /**
     * CORS - Cross-Origin Resource Sharing.
     * Allows the frontend (localhost:3000 in dev) to call the API.
     * In production, replace allowedOrigins with the actual domain.
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration().apply {
            allowedOriginPatterns = listOf("http://localhost:3000", "http://localhost:5173", "http://192.168.100.*:3000")
            allowedHeaders = listOf("*")
            allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            allowCredentials = true
            maxAge = 3600L  // Cachea el preflight OPTIONS por 1 hora
        }
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", config)
        }
    }
}