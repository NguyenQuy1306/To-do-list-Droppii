package com.interviewproject.todolist.config;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
        private final AuthenticationProvider authenticationProvider;

        private static final String[] WHITE_LIST_URL = {
                        "/v2/api-docs",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/v3/api-docs/swagger-config",
                        "/configuration/ui",
                        "/configuration/security",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/swagger-ui.html",
        };

        @Autowired
        void registerProvider(AuthenticationManagerBuilder auth) {
                auth.authenticationProvider(authenticationProvider);
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(authz -> authz

                                                .requestMatchers(WHITE_LIST_URL).permitAll()
                                                // white-listed URLs
                                                .requestMatchers(HttpMethod.DELETE, "/api/tasks/**").permitAll()
                                                .anyRequest().authenticated())
                                .cors(cors -> {
                                })
                                .csrf(csrf -> csrf.disable())
                                .formLogin(form -> form.disable())
                                .authenticationProvider(authenticationProvider);

                return http.build();
        }

}
