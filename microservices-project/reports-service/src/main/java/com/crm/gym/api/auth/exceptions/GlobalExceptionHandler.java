package com.crm.gym.api.auth.exceptions;

import com.crm.gym.api.auth.util.ProblemDetailsFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    private ProblemDetailsFactory problemDetailsFactory;

    public GlobalExceptionHandler(ProblemDetailsFactory problemDetailsFactory)
    {
        this.problemDetailsFactory = problemDetailsFactory;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(HttpServletRequest request, ResponseStatusException ex)
    {
        return problemDetailsFactory.withDetail(
                ex.getStatusCode().value(),
                request.getRequestURI(),
                ex.getReason()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException ex)
    {
        List<Map<String, String>> invalidParams = ex.getBindingResult().getFieldErrors().stream()
                .map(
                        error -> Map.of(
                                "name", error.getField(),
                                "reason", error.getDefaultMessage()
                        )
                )
                .collect(Collectors.toList());

        return problemDetailsFactory.withInvalidParams(
                ex.getStatusCode().value(),
                request.getRequestURI(),
                invalidParams
        );
    }
}