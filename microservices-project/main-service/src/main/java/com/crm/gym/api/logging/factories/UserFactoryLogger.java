package com.crm.gym.api.logging.factories;

import com.crm.gym.api.entities.User;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public abstract class UserFactoryLogger<S extends User>
{
    private final Logger logger;
    protected final String IDENTIFIABLE = "com.crm.gym.repositories.interfaces.Identifiable";

    private boolean showUsername;
    private boolean showPassword;

    public UserFactoryLogger(Logger logger) {this.logger = logger;}

    protected abstract Class<S> getUserClass();

    // Pointcuts

    public abstract void target_UserFactory();

    @Pointcut("within(com.crm.gym.factories.UserFactory+)")
    public void within_UserFactorySubclasses(){}

    @Pointcut("execution("+IDENTIFIABLE+" recreate("+IDENTIFIABLE+"))")
    public void recreate(){}

    // Advices

    @AfterReturning(
            pointcut = "target_UserFactory() && within_UserFactorySubclasses() && recreate()",
            returning = "entity")
    public void afterReturning_recreate(Object entity)
    {
        S user = (S) entity;
        String username = user.getUsername();
        String password = user.getPassword();

        if(!showUsername){username = "*".repeat(username.length());}
        if(!showPassword){password = "*".repeat(password.length());}

        logger.info("Generated {} (username:{}, password:{})",
                getUserClass().getSimpleName(), username, password);
    }

    @Autowired
    private void setShowUsername(@Value("${logging.user-credentials.show-username:false}") boolean showUsername)
    {
        this.showUsername = showUsername;
    }

    @Autowired
    private void setShowPassword(@Value("${logging.user-credentials.show-password:false}") boolean showPassword)
    {
        this.showPassword = showPassword;
    }
}
