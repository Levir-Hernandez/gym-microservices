package com.crm.gym.api.config.actuators;

import org.springframework.core.env.Environment;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Component("storageInitResources")
public class StorageInitResourcesHealthIndicator implements HealthIndicator
{
    private Environment env;
    private List<String> entities;

    public StorageInitResourcesHealthIndicator(Environment env, List<String> entities)
    {
        this.env = env;
        this.entities = entities;
    }

    @Override
    public Health health()
    {
        Map<String, Object> details = entities.stream()
                .collect(
                        Collectors.toMap(
                                entity -> entity,
                                this::buildDetailsForEntity
                        )
                );

        return Health.up()
            .withDetails(details)
            .build();
    }

    private Map<String, Object> buildDetailsForEntity(String entity)
    {
        String envProp = String.format("storage.%s.path", entity);

        String path = env.getProperty(envProp);
        boolean available;

        Map<String, Object> subDetails = new LinkedHashMap<>();
        if(Objects.nonNull(path))
        {
            subDetails.put("path", path);

            Resource resource = new ClassPathResource(path);
            available = resource.exists() && resource.isReadable();
        }
        else
        {
            available = false;
        }

        subDetails.put("available", available);

        return subDetails;
    }
}
