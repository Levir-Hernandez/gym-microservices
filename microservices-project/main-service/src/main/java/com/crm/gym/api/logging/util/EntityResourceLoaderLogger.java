package com.crm.gym.api.logging.util;

import java.util.List;
import java.util.Objects;

import org.aspectj.lang.JoinPoint;
import com.crm.gym.api.util.EntityResourceLoader;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.AfterReturning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
@Component
public class EntityResourceLoaderLogger
{
    private final String LIST = "java.util.List";

    private final Logger logger = LoggerFactory.getLogger(EntityResourceLoader.class);

    @Pointcut("within(com.crm.gym.api.util.EntityResourceLoader+) && execution("+LIST+" loadEntitiesFromJson(String,Class))")
    public void loadEntitiesFromJson() {}

    @AfterReturning(pointcut = "loadEntitiesFromJson()", returning = "entities")
    public void afterReturning_loadEntitiesFromJson(JoinPoint jp, List<?> entities)
    {
        Object[] args = jp.getArgs();
        String entitiesPath = args[0].toString();
        Class<?> entityClass = (Class<?>) args[1];

        if(Objects.nonNull(entities))
        {
            logger.info("{}s defined in '{}' loaded into memory", entityClass.getSimpleName(), entitiesPath);
        }
        else
        {
            logger.warn("Unable to load {}s. File '{}' is missing or unreadable", entityClass.getSimpleName(), entitiesPath);
        }
    }
}
