package com.crm.gym.api.logging.repositories;

import com.crm.gym.api.repositories.interfaces.Identifiable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import java.util.Objects;
import java.util.Optional;

public abstract class TemplateRepositoryLogger<Id, Entity extends Identifiable<Id>>
{
    protected final Logger logger;
    protected final String OPTIONAL = "java.util.Optional";
    protected final String IDENTIFIABLE = "com.crm.gym.repositories.interfaces.Identifiable";

    public TemplateRepositoryLogger(Logger logger)
    {
        this.logger = logger;
    }

    protected abstract Class<Entity> getEntityClass();

    // Pointcuts

    public abstract void target_EntityRepository();

    @Pointcut("within(com.crm.gym.repositories.interfaces.TemplateRepository+)")
    public void within_TemplateRepositorySubclasses(){}

    @Pointcut("execution("+IDENTIFIABLE+" create("+IDENTIFIABLE+"))")
    public void create() {}

    @Pointcut("execution("+IDENTIFIABLE+" update(Object," +IDENTIFIABLE+ "))")
    public void update() {}

    @Pointcut("execution(boolean deleteIfExists(Object))")
    public void delete() {}

    @Pointcut("execution("+OPTIONAL+" findById(Object))")
    public void findById() {}

    // Advices

    @Around("target_EntityRepository() && within_TemplateRepositorySubclasses() && create()")
    public Entity around_create(ProceedingJoinPoint jp) throws Throwable
    {
        logger.info("Storing new {} in database", getEntityClass().getSimpleName());
        Entity entity = (Entity) jp.proceed();
        logger.info("Stored new {} {} in database", getEntityClass().getSimpleName(), entity.getId());

        return entity;
    }

    @Around("target_EntityRepository() && within_TemplateRepositorySubclasses() && update()")
    public Entity around_updateById(ProceedingJoinPoint jp) throws Throwable
    {
        return around_update(jp);
    }

    @Around("target_EntityRepository() && within_TemplateRepositorySubclasses() && delete()")
    public boolean around_deleteById(ProceedingJoinPoint jp) throws Throwable
    {
        return around_delete(jp);
    }

    @Around("target_EntityRepository() && within_TemplateRepositorySubclasses() && findById()")
    public Optional<Entity> around_findById(ProceedingJoinPoint jp) throws Throwable
    {
        return around_findBy(jp);
    }

    protected Entity around_update(ProceedingJoinPoint jp) throws Throwable
    {
        Object[] args = jp.getArgs();
        Object field = args[0];

        logger.info("Saving changes for {} {} in database", getEntityClass().getSimpleName(), field);
        Entity entity = (Entity) jp.proceed();

        if(Objects.nonNull(entity))
        {
            logger.info("{} {} changed in database", getEntityClass().getSimpleName(), entity.getId());
        }
        else
        {
            logger.warn("{} {} does not exist. Update skipped", getEntityClass().getSimpleName(), field);
        }

        return entity;
    }

    protected boolean around_delete(ProceedingJoinPoint jp) throws Throwable
    {
        Object[] args = jp.getArgs();
        Object field = args[0];

        boolean deleted = (boolean) jp.proceed();
        if(deleted)
        {
            logger.info("{} {} deleted in database", getEntityClass().getSimpleName(), field);
        }
        else
        {
            logger.warn("{} {} does not exist. Delete skipped", getEntityClass().getSimpleName(), field);
        }
        return deleted;
    }

    protected Optional<Entity> around_findBy(ProceedingJoinPoint jp) throws Throwable
    {
        Object[] args = jp.getArgs();
        Object field = args[0];

        Optional<Entity> entity = (Optional<Entity>) jp.proceed();
        if(entity.isPresent())
        {
            logger.info("Fetching {} {} from database", getEntityClass().getSimpleName(), field);
        }
        else
        {
            logger.warn("{} {} not found. Returning null", getEntityClass().getSimpleName(), field);
        }
        return entity;
    }
}