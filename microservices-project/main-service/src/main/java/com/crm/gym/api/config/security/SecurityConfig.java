package com.crm.gym.api.config.security;

import com.crm.gym.api.auth.filters.GlobalExceptionHandlerFilter;
import com.crm.gym.api.auth.filters.JwtAuthFilter;
import com.crm.gym.api.auth.services.JwtTokenService;
import com.crm.gym.api.auth.services.TokenBlacklistService;
import com.crm.gym.api.auth.util.ProblemDetailsFactory;
import com.crm.gym.api.controllers.exceptions.GlobalExceptionHandler;
import com.crm.gym.api.controllers.exceptions.PermissionDeniedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class SecurityConfig
{
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain publicAccessFilterChain(HttpSecurity http) throws Exception
    {
        List<RequestMatcher> publicEndpointsMatchers = Stream.concat(
                        // Allows open access to OpenAPI docs
                        Stream.of("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-resources/**", "/webjars/**")
                                .map(AntPathRequestMatcher::new),
                        // Allows open access to these endpoints per business rules
                        Stream.of("/trainees", "/trainers", "/*/login", "/*/refresh")
                                .map(pattern -> new AntPathRequestMatcher(pattern, HttpMethod.POST.name()))
                        )
                .collect(Collectors.toList());

        publicEndpointsMatchers.add(
                Optional.of("/*/change-password")
                .map(pattern -> new AntPathRequestMatcher(pattern, HttpMethod.PUT.name()))
                .get()
        );

        return http
            .securityMatcher(new OrRequestMatcher(publicEndpointsMatchers))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .build();
    }

    @Bean
    public SecurityFilterChain jwtAuthenticationFilterChain(HttpSecurity http,
                                                            JwtTokenService jwtTokenManager, TokenBlacklistService tokenBlacklistService,
                                                            ObjectMapper objectMapper, GlobalExceptionHandler globalExceptionHandler,
                                                            AuthenticationEntryPoint authenticationEntryPoint,
                                                            AccessDeniedHandler accessDeniedHandler) throws Exception
    {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers("/trainees/**").hasRole("TRAINEE")
                                .requestMatchers("/trainers/**").hasRole("TRAINER")
                                .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .addFilterBefore(new JwtAuthFilter(jwtTokenManager, tokenBlacklistService), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new GlobalExceptionHandlerFilter(objectMapper, globalExceptionHandler), JwtAuthFilter.class)
                .build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper, ProblemDetailsFactory problemDetailsFactory)
    {
        return (request, response, authException) ->
        {
            int status = HttpServletResponse.SC_UNAUTHORIZED; // 401
            Map<String, Object> body = problemDetailsFactory.withDetail(
                    status,
                    request.getRequestURI(),
                    "Bearer Authentication is required to access this resource"
            ).getBody();

            response.setStatus(status);
            response.setContentType("application/json");
            objectMapper.writeValue(response.getWriter(), body);
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(ObjectMapper objectMapper, ProblemDetailsFactory problemDetailsFactory)
    {
        return (request, response, accessDeniedException) ->
        {
            PermissionDeniedException ex = new PermissionDeniedException(accessDeniedException);

            int status = ex.getStatusCode().value();
            Map<String, Object> body = problemDetailsFactory.withDetail(
                    status,
                    request.getRequestURI(),
                    ex.getReason()
            ).getBody();

            response.setStatus(status);
            response.setContentType("application/json");
            objectMapper.writeValue(response.getWriter(), body);
        };
    }
}
