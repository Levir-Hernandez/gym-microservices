package com.crm.gym.api.util;

import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.core.io.ClassPathResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class EntityResourceLoader
{
    private ObjectMapper mapper;

    public EntityResourceLoader(ObjectMapper mapper)
    {
        this.mapper = mapper;
    }

    public <T> List<T> loadEntitiesFromJson(String entitiesPath, Class<T> entityClass)
    {
        Resource entitiesFile = new ClassPathResource(entitiesPath);

        CollectionType collectionType = mapper.getTypeFactory()
                .constructCollectionType(List.class, entityClass);

        List<T> entities;

        try {entities = mapper.readValue(entitiesFile.getInputStream(), collectionType);}
        catch (IOException e) {entities = null;}

        return entities;
    }
}
