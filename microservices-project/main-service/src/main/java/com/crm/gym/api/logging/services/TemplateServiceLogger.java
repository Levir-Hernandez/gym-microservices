package com.crm.gym.api.logging.services;

import com.crm.gym.api.repositories.interfaces.Identifiable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import java.util.Objects;

public abstract class TemplateServiceLogger<Id, Entity extends Identifiable<Id>>
{
    protected final Logger logger;
    private final String IDENTIFIABLE = "com.crm.gym.repositories.interfaces.Identifiable";

    public TemplateServiceLogger(Logger logger)
    {
        this.logger = logger;
    }

    protected abstract Class<Entity> getEntityClass();

    // Pointcuts

    public abstract void target_EntityService();

    @Pointcut("within(com.crm.gym.api.services.TemplateService+)")
    public void within_TemplateServiceSubclasses() {}

    @Pointcut("execution("+IDENTIFIABLE+" saveEntity("+IDENTIFIABLE+"))")
    public void saveEntity() {}

    @Pointcut("execution("+IDENTIFIABLE+" updateEntity(Object," + IDENTIFIABLE + "))")
    public void updateEntity() {}

    @Pointcut("execution(boolean deleteEntity(Object))")
    public void deleteEntity() {}

    @Pointcut("execution("+IDENTIFIABLE+" getEntityById(Object))")
    public void getEntityById() {}

    // Advices

    @Around("target_EntityService() && within_TemplateServiceSubclasses() && saveEntity()")
    public Entity around_saveEntity(ProceedingJoinPoint jp) throws Throwable
    {
        logger.info("Creating new {}", getEntityClass().getSimpleName());
        Entity entity = (Entity) jp.proceed();
        logger.info("Created new {} {}", getEntityClass().getSimpleName(), entity.getId());

        return entity;
    }

    @Around("target_EntityService() && within_TemplateServiceSubclasses() && getEntityById()")
    public Entity around_getEntityById(ProceedingJoinPoint jp) throws Throwable
    {
        return around_getEntity(jp);
    }

    protected Entity around_updateEntity(ProceedingJoinPoint jp) throws Throwable
    {
        Object[] args = jp.getArgs();
        Object field = args[0];

        logger.info("Updating {} {}", getEntityClass().getSimpleName(), field);
        Entity entity = (Entity) jp.proceed(args);

        if(Objects.nonNull(entity))
        {
            logger.info("{} {} updated", getEntityClass().getSimpleName(), field);
        }

        return entity;
    }

    protected boolean around_deleteEntity(ProceedingJoinPoint jp) throws Throwable
    {
        Object[] args = jp.getArgs();
        Object field = args[0];

        logger.info("Deleting {} {}", getEntityClass().getSimpleName(), field);

        boolean deleted = (boolean) jp.proceed(args);
        if(deleted)
        {
            logger.info("{} {} deleted", getEntityClass().getSimpleName(), field);
        }

        return deleted;
    }

    protected Entity around_getEntity(ProceedingJoinPoint jp) throws Throwable
    {
        Object[] args = jp.getArgs();
        Object field = args[0];

        logger.info("Searching {} {}", getEntityClass().getSimpleName(), field);

        Entity entity = (Entity) jp.proceed(args);
        if(Objects.nonNull(entity))
        {
            logger.info("{} {} retrieved", getEntityClass().getSimpleName(), field);
        }

        return entity;
    }
}
