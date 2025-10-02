package com.crm.gym.api.logging.util;

import com.crm.gym.api.util.PasswordGenerator;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PasswordGeneratorLogger
{
    private final Logger logger = LoggerFactory.getLogger(PasswordGenerator.class);
    private boolean showPassword;

    public PasswordGeneratorLogger(@Value("${logging.user-credentials.show-password:false}") boolean showPassword)
    {
        this.showPassword = showPassword;
    }

    @Pointcut("within(com.crm.gym.api.util.PasswordGenerator+) && execution(String generatePassword())")
    public void generatePassword() {}

    @AfterReturning(pointcut = "generatePassword()", returning = "password")
    public void afterReturning_generateUsername(String password)
    {
        if(!showPassword){password = "*".repeat(password.length());}
        logger.debug("Generated new password: {}", password);
    }
}
