package com.crm.gym.api.logging.services;

import com.crm.gym.api.entities.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.UUID;

public abstract class UserServiceLogger<S extends User> extends TemplateServiceLogger<UUID, S>
{
    private final String USER = "com.crm.gym.entities.User";

    public UserServiceLogger(Logger logger)
    {
        super(logger);
    }

    // Pointcuts

    @Pointcut("execution("+USER+" getUserByUsername(String))")
    public void getUserByUsername() {}

    @Pointcut("execution("+USER+" updateUserByUsername(String,"+USER+"))")
    public void updateUserByUsername() {}

    @Pointcut("execution(Boolean activateUser(String))")
    public void activateUser() {}

    @Pointcut("execution(Boolean deactivateUser(String))")
    public void deactivateUser() {}

    @Pointcut("execution(boolean login(String,String))")
    public void login() {}

    @Pointcut("execution(boolean changePassword(String,String,String))")
    public void changePassword() {}

    // Advices

    @Around("target_EntityService() && getUserByUsername()")
    public S around_getUserByUsername(ProceedingJoinPoint jp) throws Throwable
    {
        return around_getEntity(jp);
    }

    @Around("target_EntityService() && updateUserByUsername()")
    public S around_updateUserByUsername(ProceedingJoinPoint jp) throws Throwable
    {
        return around_updateEntity(jp);
    }

    @Around("target_EntityService() && activateUser()")
    public Boolean around_activateUser(ProceedingJoinPoint jp) throws Throwable
    {
        return around_updateUserIsActive(
                "Activating",
                "activated",
                "active", jp);
    }

    @Around("target_EntityService() && deactivateUser()")
    public Boolean around_deactivateUser(ProceedingJoinPoint jp) throws Throwable
    {
        return around_updateUserIsActive(
                "Deactivating",
                "deactivated",
                "inactive", jp);
    }

    @Around("target_EntityService() && login()")
    public boolean around_login(ProceedingJoinPoint jp) throws Throwable
    {
        Object[] args = jp.getArgs();
        String username = (String) args[0];

        logger.info("Checking credentials for {} {}", getEntityClass().getSimpleName(), username);

        boolean logged = (boolean) jp.proceed();

        if(logged)
        {
            logger.info("{} {} logged in", getEntityClass().getSimpleName(), username);
        }
        else
        {
            logger.warn("Invalid credentials for {} {}", getEntityClass().getSimpleName(), username);
        }

        return logged;
    }

    @Around("target_EntityService() && changePassword()")
    public boolean around_changePassword(ProceedingJoinPoint jp) throws Throwable
    {
        Object[] args = jp.getArgs();
        String username = (String) args[0];

        logger.info("Attempting to change password for {} {}", getEntityClass().getSimpleName(), username);

        boolean passwordChanged = (boolean) jp.proceed();
        if(passwordChanged)
        {
            logger.info("Password changed for {} {}", getEntityClass().getSimpleName(), username);
        }
        else
        {
            logger.warn("Invalid current credentials for {} {}. Password not changed", getEntityClass().getSimpleName(), username);
        }

        return passwordChanged;
    }

    private Boolean around_updateUserIsActive(String action, String changedState, String unchangedState, ProceedingJoinPoint jp) throws Throwable
    {
        Object[] args = jp.getArgs();
        String username = (String) args[0];

        logger.info("{} {} {}", action, getEntityClass().getSimpleName(), username);

        Boolean userActivated = (Boolean) jp.proceed();
        if(Objects.nonNull(userActivated))
        {
            if(userActivated)
            {
                logger.info("{} {} {}", getEntityClass().getSimpleName(), username, changedState);
            }
            else
            {
                logger.warn("{} {} is already {}. Update skipped", getEntityClass().getSimpleName(), username, unchangedState);
            }
        }
        else
        {
            logger.warn("{} {} not found. Returning null", getEntityClass().getSimpleName(), username);
        }

        return userActivated;
    }
}
