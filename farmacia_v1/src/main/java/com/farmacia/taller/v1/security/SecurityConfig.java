package com.farmacia.taller.v1.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .cors(org.springframework.security.config.Customizer.withDefaults())
            .csrf(csrf -> csrf.disable()) // Desactivar CSRF para APIs REST stateless
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                // Sin token o token invalido/expirado: identidad no establecida -> 401
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                // Identidad valida pero sin privilegios suficientes -> 403
                .accessDeniedHandler((request, response, accessDeniedException) ->
                    response.sendError(HttpServletResponse.SC_FORBIDDEN))
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll() // Endpoints de login/registro publicos
                .requestMatchers("/h2-console/**").permitAll()
                // El reenvio interno a /error (tras sendError o una excepcion no capturada) debe
                // conservar el codigo de estado ya decidido, sin volver a evaluar autenticacion/rol
                .requestMatchers("/error").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/medicamentos/**").hasRole("ADMIN") // Solo ADMIN crea
                .requestMatchers(HttpMethod.PUT, "/api/v1/medicamentos/**").hasRole("ADMIN") // Solo ADMIN edita
                .requestMatchers(HttpMethod.DELETE, "/api/v1/medicamentos/**").hasRole("ADMIN") // Solo ADMIN elimina
                .requestMatchers(HttpMethod.GET, "/api/v1/medicamentos/**").hasAnyRole("ADMIN", "USER") // ADMIN y USER leen
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
