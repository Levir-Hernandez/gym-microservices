package com.crm.gym.api.config.actuators;

import org.springframework.web.client.RestTemplate;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component("apiDocs")
public class ApiDocsHealthIndicator implements HealthIndicator
{
    private String apiDocsUrl;
    private RestTemplate restTemplate;

    public ApiDocsHealthIndicator(@Value("${springdoc.api-docs.path:/v3/api-docs}") String apiDocsUrl,
                                  RestTemplate restTemplate)
    {
        this.apiDocsUrl = apiDocsUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public Health health()
    {
        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(apiDocsUrl)
                .build()
                .toUriString();

        boolean isApiDocsAvailable = restTemplate.getForEntity(url, String.class)
                .getStatusCode()
                .is2xxSuccessful();

        Health.Builder healthBuilder = isApiDocsAvailable? Health.up() : Health.down();

        return healthBuilder
                .withDetail("url", url)
                .build();
    }
}
