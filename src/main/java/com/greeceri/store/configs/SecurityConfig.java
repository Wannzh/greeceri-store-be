package com.greeceri.store.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;

        @SuppressWarnings("removal")
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.cors();
                http.csrf(AbstractHttpConfigurer::disable)

                                .authorizeHttpRequests(auth -> auth
                                                // public
                                                .requestMatchers(
                                                                "/api/auth/**",
                                                                "/v3/api-docs/**",
                                                                "/v3/api-docs",
                                                                "/swagger-ui.html",
                                                                "/swagger-ui/**",
                                                                "/webjars/swagger-ui/**",
                                                                "/api/payments/callback",
                                                                "/api/products",
                                                                "/api/products/**",
                                                                "/api/categories") // Jangan lupa tambahin
                                                                                          // endpoint untuk
                                                // callback xendit
                                                .permitAll()

                                                // Admin + User
                                                .requestMatchers(
                                                                "/api/user/profile"
                                                )
                                                .hasAnyAuthority("ADMIN", "USER")

                                                // User
                                                .requestMatchers(
                                                                "/api/user/address",
                                                                "/api/user/addres/**"
                                                )
                                                .hasAnyAuthority("USER")

                                                .anyRequest().authenticated())

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
                ;

                return http.build();
        }
}