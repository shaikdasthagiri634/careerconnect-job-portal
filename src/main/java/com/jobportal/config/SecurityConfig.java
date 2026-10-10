package com.jobportal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Two security chains:
 *  1. /api/**  - stateless REST API secured with HTTP Basic (easy to test from Postman).
 *  2. Website  - session based form login with CSRF protection.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/jobs", "/api/jobs/*").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/employer/**").hasRole("EMPLOYER")
                .requestMatchers(HttpMethod.POST, "/api/jobs").hasRole("EMPLOYER")
                .requestMatchers(HttpMethod.PUT, "/api/jobs/*").hasRole("EMPLOYER")
                .requestMatchers(HttpMethod.PATCH, "/api/jobs/*/toggle").hasRole("EMPLOYER")
                .requestMatchers(HttpMethod.DELETE, "/api/jobs/*").hasAnyRole("EMPLOYER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/jobs/*/applications").hasRole("EMPLOYER")
                .requestMatchers(HttpMethod.POST, "/api/jobs/*/apply").hasRole("CANDIDATE")
                .requestMatchers(HttpMethod.PATCH, "/api/applications/*/status").hasRole("EMPLOYER")
                .requestMatchers("/api/applications/me", "/api/applications/*").hasRole("CANDIDATE")
                .anyRequest().authenticated())
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webSecurity(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/jobs", "/jobs/*", "/register", "/login", "/about",
                        "/css/**", "/js/**", "/images/**", "/favicon.ico", "/error").permitAll()
                .requestMatchers("/jobs/*/apply", "/candidate/**").hasRole("CANDIDATE")
                .requestMatchers("/employer/**").hasRole("EMPLOYER")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error")
                .permitAll())
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll())
            .exceptionHandling(ex -> ex.accessDeniedPage("/access-denied"));
        return http.build();
    }
}
