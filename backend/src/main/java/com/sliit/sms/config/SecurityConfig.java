package com.sliit.sms.config;

import com.sliit.sms.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * FR1 - User Authentication & Authorisation.
 * Stateless JWT security (no session/cookie) so the React SPA authenticates
 * with a Bearer token on every request instead of a server-side session.
 * Role-based access control (RBAC) is enforced both at the URL level here,
 * and (for "my own record only" style rules that a URL pattern can't
 * express) inside the controllers/services using SecurityUtil.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/error/**", "/uploads/**").permitAll()

                // Employee Records Management (FR2) - Admin & HR Manager manage
                .requestMatchers("/api/employees/**").hasAnyAuthority(Role.ADMIN.name(), Role.HR_MANAGER.name())
                .requestMatchers("/api/departments/**").hasAnyAuthority(Role.ADMIN.name(), Role.HR_MANAGER.name())

                // Payroll Management (FR5) - Payroll Officer generates; everyone can view their own payslips under /api/payroll/my
                .requestMatchers("/api/payroll/generate", "/api/payroll/run/**", "/api/payroll/all", "/api/payroll/eligible-employees")
                        .hasAuthority(Role.PAYROLL_OFFICER.name())

                // Performance Management (FR6) - Supervisors & HR Manager set goals and run appraisals; everyone can read their own
                .requestMatchers(HttpMethod.POST, "/api/performance/goal/new", "/api/performance/review/new")
                        .hasAnyAuthority(Role.SUPERVISOR.name(), Role.HR_MANAGER.name())
                .requestMatchers(HttpMethod.PUT, "/api/performance/goal/*")
                        .hasAnyAuthority(Role.SUPERVISOR.name(), Role.HR_MANAGER.name())

                // Shift & Roster Scheduling (FR7) - Supervisors build the roster
                .requestMatchers("/api/schedule/manage/**").hasAnyAuthority(Role.SUPERVISOR.name(), Role.ADMIN.name())

                // Reporting & Dashboards (FR9) - exports restricted to management roles
                .requestMatchers("/api/reports/**").hasAnyAuthority(Role.ADMIN.name(), Role.HR_MANAGER.name(), Role.PAYROLL_OFFICER.name())

                // Leave approvals - Supervisors and HR Managers
                .requestMatchers("/api/leave/approvals", "/api/leave/decide").hasAnyAuthority(Role.SUPERVISOR.name(), Role.HR_MANAGER.name())

                // Everything else just requires a valid token; fine-grained
                // "is this MY record" checks happen inside the controllers.
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
