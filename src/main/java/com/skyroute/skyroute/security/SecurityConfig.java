package com.skyroute.skyroute.security;

import com.skyroute.skyroute.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/h2-console/**", "/swagger-ui/**",
                                                                "/v3/api-docs/**"))
                                .headers(headers -> headers
                                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/airports/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/flights/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/routes/**").permitAll()
                                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                                .requestMatchers("/h2-console/**").permitAll()

                                                .requestMatchers(HttpMethod.POST, "/api/users", "/api/aircrafts",
                                                                "/api/airports",
                                                                "/api/routes", "/api/flights")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/users/**", "/api/aircrafts/**",
                                                                "/api/airports/**",
                                                                "/api/routes/**", "/api/flights/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/users/**",
                                                                "/api/aircrafts/**", "/api/airports/**",
                                                                "/api/routes/**", "/api/flights/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.GET, "/api/users", "/api/bookings")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.GET, "/api/bookings/flight/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(HttpMethod.PUT, "/api/users/profile").authenticated()
                                                .requestMatchers(HttpMethod.GET, "/api/bookings/user/*").authenticated()
                                                .requestMatchers(HttpMethod.POST, "/api/bookings").authenticated()

                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }
}
