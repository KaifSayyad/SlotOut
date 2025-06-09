package com.slotout.v1.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // SWAGGER & OPENAPI - must be at the top!
                .requestMatchers(
                    "/v3/api-docs",
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/swagger-ui/**"
                ).permitAll()
                // Public endpoints - no authentication required
                .requestMatchers("/api/tenants/preRegister").permitAll()  // POST for registration
                .requestMatchers("/api/tenants/verifyOtp").permitAll()  // POST for OTP verification
                .requestMatchers("/api/tenants/login").permitAll()  // POST for login
                .requestMatchers("/api/otp/**").permitAll()   // All OTP operations
                .requestMatchers("/api/services/*/timeslots/available").permitAll()  // View available slots
                .requestMatchers("/api/bookings").permitAll()  // POST for customer bookings
                .requestMatchers("/api/bookings/*/confirm").permitAll()  // POST for booking confirmation
                .requestMatchers("/health", "/actuator/**").permitAll()
                
                // Protected endpoints - authentication required (Admin/Tenant operations)
                .requestMatchers("/api/tenants/**").authenticated()  // Tenant management
                .requestMatchers("/api/services/**").authenticated()  // Service management
                .requestMatchers("/api/timeslots/**").authenticated()  // Timeslot management
                .requestMatchers("/api/bookings/**").authenticated()  // Booking management (except POST)
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(form -> form.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

