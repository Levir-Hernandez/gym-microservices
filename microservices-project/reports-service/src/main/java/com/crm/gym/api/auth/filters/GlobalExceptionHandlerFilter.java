package com.crm.gym.api.auth.filters;

import com.crm.gym.api.auth.exceptions.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

public class GlobalExceptionHandlerFilter extends OncePerRequestFilter
{
    private ObjectMapper objectMapper;
    private GlobalExceptionHandler globalExceptionHandler;

    public GlobalExceptionHandlerFilter(ObjectMapper objectMapper, GlobalExceptionHandler globalExceptionHandler)
    {
        this.objectMapper = objectMapper;
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        try
        {
            filterChain.doFilter(request, response);
        }
        catch (ResponseStatusException ex)
        {
            ResponseEntity<?> responseEntity = globalExceptionHandler.handleResponseStatusException(request, ex);

            response.setStatus(responseEntity.getStatusCode().value());
            response.setContentType("application/json");

            objectMapper.writeValue(response.getWriter(), responseEntity.getBody());
        }
    }
}
