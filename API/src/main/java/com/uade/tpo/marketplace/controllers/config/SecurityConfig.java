package com.uade.tpo.marketplace.controllers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                        .csrf(AbstractHttpConfigurer::disable)
                        .authorizeHttpRequests(req -> req
                        // Auth libre
                        .requestMatchers("/auth/**").permitAll()

                        // Cart -> solo usuarios autenticados con rol USER
                        .requestMatchers("/cart/**").hasRole("USER")

                        // Categories -> todos pueden ver y crear
                        .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/categories/**").hasRole("USER") 

                        // Products -> GET cualquiera, POST cualquiera, pero PUT/DELETE solo dueño (se valida en service)
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/products/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/products/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("USER")

                        .anyRequest().authenticated())
                        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                        .authenticationProvider(authenticationProvider)
                        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
