package com.crm.gym.api.auth.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.List;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.function.Consumer;

// Uses the RFC 7807 format
@Component
public class ProblemDetailsFactory
{
    public ResponseEntity<Map<String, Object>> withDetail(int status, String requestURI, String detail)
    {
        return withCustomizer(
                status, requestURI, body -> body.put("detail", detail)
        );
    }

    public ResponseEntity<Map<String, Object>> withInvalidParams(int status, String requestURI, List<Map<String, String>> invalidParams)
    {
        return withCustomizer(
                status, requestURI, body -> body.put("invalid-params", invalidParams)
        );
    }

    private ResponseEntity<Map<String, Object>> withCustomizer(int status, String requestURI, Consumer<Map<String, Object>> customizer)
    {
        String timestamp = Instant.now().toString();

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("title", HttpStatus.valueOf(status).getReasonPhrase());
        body.put("status", status);
        customizer.accept(body);
        body.put("instance", requestURI);
        body.put("timestamp", timestamp);

        return ResponseEntity.status(status).body(body);
    }
}
