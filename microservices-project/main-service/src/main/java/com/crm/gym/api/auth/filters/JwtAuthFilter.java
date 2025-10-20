package com.crm.gym.api.auth.filters;

import com.crm.gym.api.auth.services.JwtTokenService;
import com.crm.gym.api.auth.services.TokenBlacklistService;
import com.crm.gym.api.exceptions.UnexpectedTokenTypeException;
import com.crm.gym.api.exceptions.RevokedTokenException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class JwtAuthFilter extends OncePerRequestFilter
{
    private JwtTokenService jwtTokenManager;
    private TokenBlacklistService tokenBlacklistService;

    public JwtAuthFilter(JwtTokenService jwtTokenManager, TokenBlacklistService tokenBlacklistService)
    {
        this.jwtTokenManager = jwtTokenManager;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException
    {
        String authHeader = request.getHeader("Authorization");

        if(
                Objects.isNull(SecurityContextHolder.getContext().getAuthentication()) &&
                Objects.nonNull(authHeader) &&
                authHeader.startsWith("Bearer ")
        )
        {
            String token = authHeader.substring(7);
            Claims claims = jwtTokenManager.decodeToken(token);

            String type = claims.get("type", String.class);
            if(!type.equals("access")) {throw new UnexpectedTokenTypeException();}

            String jti = claims.getId();
            if(tokenBlacklistService.isTokenBlacklisted(jti)) {throw new RevokedTokenException();}

            Collection<GrantedAuthority> authorities = Optional.ofNullable(claims.get("role", String.class))
                    .map(role -> new SimpleGrantedAuthority("ROLE_"+role))
                    .map(List::<GrantedAuthority>of)
                    .orElse(null);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    claims, null, authorities
            );

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
