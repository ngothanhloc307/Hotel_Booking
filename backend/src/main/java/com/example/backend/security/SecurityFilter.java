package com.example.backend.security;

import com.example.backend.exceptions.CustomAccessDenialHandler;
import com.example.backend.exceptions.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityFilter {

    private final AuthFilter authFilter;
    private final CustomAccessDenialHandler accessDenialHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, CustomAccessDenialHandler customAccessDenialHandler, CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .exceptionHandling(exception
                        -> exception.accessDeniedHandler(customAccessDenialHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                .authorizeHttpRequests(request ->
                        request.requestMatchers("/api/auth/**", "api/rooms/**", "api/bookings/**").permitAll()
                                .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
