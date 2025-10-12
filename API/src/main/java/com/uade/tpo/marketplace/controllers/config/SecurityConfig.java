package com.uade.tpo.marketplace.controllers.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.uade.tpo.marketplace.exceptions.CustomAuthenticationEntryPoint;

/**
 * Configuración de seguridad con:
 * - CORS abierto para cualquier puerto en localhost/127.0.0.1
 * - JWT (stateless)
 * - Rutas públicas y protegidas
 */
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
        return http
            .securityMatcher("/**")

            // CORS: permitir cualquier puerto en localhost/127.0.0.1
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration c = new CorsConfiguration();
                c.addAllowedOriginPattern("http://localhost:*");
                c.addAllowedOriginPattern("http://127.0.0.1:*");
                // c.addAllowedOriginPattern("http://0.0.0.0:*"); // opcional si abrís dev server en red

                c.setAllowedMethods(java.util.List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
                c.setAllowedHeaders(java.util.List.of("*"));
                // Exponer cabeceras útiles para el front
                c.setExposedHeaders(java.util.List.of("Location","Content-Disposition"));
                // No usamos cookies, así que no hace falta credenciales
                c.setAllowCredentials(false);
                return c;
            }))

            // API REST con JWT: sin CSRF
            .csrf(csrf -> csrf.disable())

            // Autorización de rutas
            .authorizeHttpRequests(req -> req
                // Preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Auth libre
                .requestMatchers("/auth/**").permitAll()

                // User info del autenticado (necesita JWT válido)
                .requestMatchers(HttpMethod.GET, "/users/current").authenticated()
                // Resto de /users requiere rol (las políticas que ya tenías)
                .requestMatchers("/users/**").hasAnyRole("USER", "ADMIN")

                // Cart
                .requestMatchers(HttpMethod.GET, "/cart/**").hasAnyRole("USER","ADMIN")
                .requestMatchers(HttpMethod.POST,"/cart/**").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/cart/**").hasRole("USER")
                .requestMatchers(HttpMethod.DELETE,"/cart/**").hasRole("USER")

                // Categories
                .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
                .requestMatchers(HttpMethod.POST,"/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE,"/categories/**").hasRole("ADMIN")

                // Products
                .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                .requestMatchers(HttpMethod.POST,"/products/**").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/products/**").hasRole("USER")
                .requestMatchers(HttpMethod.DELETE,"/products/**").hasAnyRole("USER","ADMIN")

                // Orders / Invoices
                .requestMatchers(HttpMethod.GET, "/orders/**").hasAnyRole("USER","ADMIN")
                .requestMatchers(HttpMethod.POST,"/orders/**").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/invoices/**").hasAnyRole("USER","ADMIN")

                // Imágenes públicas
                .requestMatchers(HttpMethod.GET, "/images/**").permitAll()

                // Todo lo demás autenticado
                .anyRequest().authenticated()
            )

            // Stateless y filtro JWT
            .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

            // Manejo de errores
            .exceptionHandling(ex -> ex
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)
            )

            .build();
    }
}
