package com.event.config;

import com.event.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // Plain text password encoder (for testing / non-production use)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString(); // store as plain text
            }

            @Override
            public boolean matches(CharSequence rawPassword, String storedPassword) {
                return rawPassword.toString().equals(storedPassword); // compare as-is
            }
        };
    }

    // Authentication manager
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    // Security filter chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/home", "/users/login", "/users/signup", "/css/**", "/images/**", "/error").permitAll()
                .requestMatchers("/admin/**").hasAuthority("admin")
                .requestMatchers("/student/**").hasAuthority("student")
                .requestMatchers("/attendance/admin/**").hasAuthority("admin")
                .requestMatchers("/attendance/mark/**").hasAuthority("student")
                .requestMatchers("/users/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/users/login")
                .loginProcessingUrl("/perform_login")
                .successHandler(customSuccessHandler())
                .failureUrl("/users/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/users/login?logout=true")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    // Redirect users after successful login
    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> {
            var authorities = authentication.getAuthorities().stream()
                    .map(a -> a.getAuthority().toLowerCase()) // all lowercase
                    .toList();

            if (authorities.contains("admin")) {
                response.sendRedirect("/admin/dashboard");
            } else if (authorities.contains("student")) {
                response.sendRedirect("/student/dashboard");
            } else {
                response.sendRedirect("/login?error=true");
            }
        };
    }
}