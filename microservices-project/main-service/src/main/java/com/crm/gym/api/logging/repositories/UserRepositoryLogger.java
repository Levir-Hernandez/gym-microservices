package com.crm.gym.api.logging.repositories;

import com.crm.gym.api.entities.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.UUID;

public abstract class UserRepositoryLogger<S extends User> extends TemplateRepositoryLogger<UUID, S>
{
    private final String USER = "com.crm.gym.entities.User";
    protected final String OPTIONAL = "java.util.Optional";

    public UserRepositoryLogger(Logger logger)
    {
        super(logger);
    }

    // Pointcuts

    @Pointcut("execution("+OPTIONAL+" findByUsername(String))")
    public void findByUsername() {}

    @Pointcut("execution("+USER+" updateByUsername(String,"+USER+"))")
    public void updateByUsername() {}

    @Around("target_EntityRepository() && findByUsername()")
    public Optional<S> around_findByUsername(ProceedingJoinPoint jp) throws Throwable
    {
        return super.around_findBy(jp);
    }

    @Around("target_EntityRepository() && updateByUsername()")
    public S around_updateByUsername(ProceedingJoinPoint jp) throws Throwable
    {
        return super.around_update(jp);
    }
}
