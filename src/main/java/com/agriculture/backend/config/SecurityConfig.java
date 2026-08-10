package com.agriculture.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Origines autorisées, externalisées dans application.properties
    // Exemple local : cors.allowed-origins=http://localhost:5173
    // Exemple après déploiement : cors.allowed-origins=http://localhost:5173,https://agroplateforme.vercel.app
    @Value("${cors.allowed-origins}")
    private String allowedOriginsRaw;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        List<String> origins = Arrays.stream(allowedOriginsRaw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                		.requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/regions").permitAll()
                        .requestMatchers("/api/districts/**").permitAll()

                        .requestMatchers("/api/dashboard", "/api/dashboard/**")
                            .hasAnyRole("ADMIN_NATIONAL", "RESPONSABLE_REGIONAL", "AGENT_TERRAIN")

                        .requestMatchers(HttpMethod.PATCH, "/api/agriculteurs/*/activation")
                            .hasAnyRole("ADMIN_NATIONAL", "RESPONSABLE_REGIONAL", "AGENT_TERRAIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/agriculteurs/**")
                            .hasRole("ADMIN_NATIONAL")

                        .requestMatchers(HttpMethod.POST, "/api/agriculteurs/**")
                            .hasAnyRole("ADMIN_NATIONAL", "RESPONSABLE_REGIONAL", "AGENT_TERRAIN")
                        .requestMatchers(HttpMethod.PUT, "/api/agriculteurs/**")
                            .hasAnyRole("ADMIN_NATIONAL", "RESPONSABLE_REGIONAL", "AGENT_TERRAIN")

                        .requestMatchers(HttpMethod.GET, "/api/agriculteurs/**")
                            .hasAnyRole("ADMIN_NATIONAL", "RESPONSABLE_REGIONAL", "AGENT_TERRAIN", "AGRICULTEUR")
                        
                        .requestMatchers(HttpMethod.POST, "/api/programmes/**")
                            .hasAnyRole("ADMIN_NATIONAL", "RESPONSABLE_REGIONAL")
                        .requestMatchers(HttpMethod.PUT, "/api/programmes/**")
                            .hasAnyRole("ADMIN_NATIONAL", "RESPONSABLE_REGIONAL")
                        .requestMatchers(HttpMethod.PATCH, "/api/programmes/**")
                            .hasAnyRole("ADMIN_NATIONAL", "RESPONSABLE_REGIONAL")
                        .requestMatchers(HttpMethod.DELETE, "/api/programmes/**")
                            .hasRole("ADMIN_NATIONAL")
                        .requestMatchers(HttpMethod.GET, "/api/programmes/**")
                            .hasAnyRole("ADMIN_NATIONAL", "RESPONSABLE_REGIONAL", "AGENT_TERRAIN", "AGRICULTEUR")
                        .requestMatchers(HttpMethod.GET, "/api/rapports/**")
                            .hasAnyRole("ADMIN_NATIONAL", "RESPONSABLE_REGIONAL")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}