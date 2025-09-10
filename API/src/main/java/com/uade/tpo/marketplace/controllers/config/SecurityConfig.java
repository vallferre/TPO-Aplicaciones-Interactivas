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
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.uade.tpo.marketplace.exceptions.CustomAuthenticationEntryPoint;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;
        private final AccessDeniedHandler accessDeniedHandler;
        private final CustomAuthenticationEntryPoint authenticationEntryPoint;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                
                http
                        .csrf(AbstractHttpConfigurer::disable)
                        .authorizeHttpRequests(req -> req
                        // Auth libre
                        .requestMatchers("/auth/**").permitAll()

                        // Cart -> solo usuarios autenticados con rol USER
                        .requestMatchers(HttpMethod.GET, "/cart/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/cart/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/cart/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/cart/**").hasRole("USER")

                        // Categories -> todos pueden ver y crear
                        .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/categories/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/categories/**").hasRole("ADMIN") 

                        // Products -> GET cualquiera, POST cualquiera, pero PUT/DELETE solo dueño (se valida en service)
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/products/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/products/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasAnyRole("USER", "ADMIN")

                        .requestMatchers("/users/**").hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/orders/**").hasAnyRole("USER", "ADMIN") //.
                        .requestMatchers(HttpMethod.POST, "/orders/**").hasRole("USER") //.
                        .requestMatchers(HttpMethod.GET, "/invoices/**").hasAnyRole("USER", "ADMIN") //.

                        .anyRequest().authenticated())
                        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                        .authenticationProvider(authenticationProvider)
                        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                        .exceptionHandling(ex -> ex
                                .accessDeniedHandler(accessDeniedHandler)        // 403 con JSON
                                .authenticationEntryPoint(authenticationEntryPoint) // 401 con JSON
                        );

                return http.build();
        }
}
