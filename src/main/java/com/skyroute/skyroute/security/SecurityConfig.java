package com.skyroute.skyroute.security;

import com.skyroute.skyroute.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        @Value("${cors.allowed-origins}")
        private String allowedOrigins;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .headers(headers -> headers
                                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**")
                                                .permitAll()
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .requestMatchers("/api/email/**").permitAll()
                                                .requestMatchers("/api/public/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/airports/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/flights/**").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/flights/budget").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/routes/**").permitAll()
                                                .requestMatchers("/actuator/**").permitAll()
                                                .requestMatchers("/h2-console/**").permitAll()

                                                .requestMatchers(HttpMethod.POST, "/api/users", "/api/aircrafts",
                                                                "/api/airports",
                                                                "/api/routes", "/api/flights")
                                                .hasRole("ADMIN")

                                                .requestMatchers(HttpMethod.PUT, "/api/users/profile").authenticated()

                                                .requestMatchers(HttpMethod.PUT, "/api/users/**", "/api/aircrafts/**",
                                                                "/api/airports/**",
                                                                "/api/routes/**", "/api/flights/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/users/**",
                                                                "/api/aircrafts/**", "/api/airports/**",
                                                                "/api/routes/**", "/api/flights/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.GET, "/api/users", "/api/bookings",
                                                                "/api/aircrafts", "/api/bookings/flight/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.GET, "/api/bookings/user/*",
                                                                "/api/bookings/{id}")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.POST, "/api/bookings",
                                                                "/api/bookings/{id}/confirm", "/api/bookings/{id}/cancel")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/api/bookings/{id}/passenger-names",
                                                                "/api/bookings/{id}/passenger-birth-dates",
                                                                "/api/bookings/{id}/status")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.DELETE, "/api/bookings/{id}")
                                                .authenticated()
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

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                List<String> origins = Arrays.asList(allowedOrigins.split(","));
                configuration.setAllowedOrigins(origins);

                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("*"));
                configuration.setAllowCredentials(true);
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
