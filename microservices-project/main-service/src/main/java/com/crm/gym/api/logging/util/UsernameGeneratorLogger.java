package com.crm.gym.api.logging.util;

import com.crm.gym.api.util.UsernameGenerator;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UsernameGeneratorLogger
{
    private final Logger logger = LoggerFactory.getLogger(UsernameGenerator.class);
    private boolean showUsername;

    public UsernameGeneratorLogger(@Value("${logging.user-credentials.show-username:false}") boolean showUsername)
    {
        this.showUsername = showUsername;
    }

    @Pointcut("within(com.crm.gym.api.util.UsernameGenerator+) && execution(String generateUsername())")
    public void generateUsername() {}

    @AfterReturning(pointcut = "generateUsername()", returning = "username")
    public void afterReturning_generateUsername(String username)
    {
        if(!showUsername){username = "*".repeat(username.length());}
        logger.debug("Generated new username: {}", username);
    }
}
