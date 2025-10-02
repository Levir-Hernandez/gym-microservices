package com.crm.gym.api.logging.repositories;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.crm.gym.api.entities.Trainee;

import org.slf4j.LoggerFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import com.crm.gym.api.repositories.interfaces.TraineeRepository;

@Aspect
@Component
public class TraineeRepositoryLogger extends UserRepositoryLogger<Trainee>
{
    public TraineeRepositoryLogger()
    {
        super(LoggerFactory.getLogger(TraineeRepository.class));
    }
    
    @Override
    protected Class<Trainee> getEntityClass() {return Trainee.class;}

    // Pointcuts

    @Override
    @Pointcut("target(com.crm.gym.repositories.interfaces.TraineeRepository)")
    public void target_EntityRepository() {}

    @Pointcut("execution(boolean deleteByUsernameIfExists(String))")
    public void deleteByUsernameIfExists() {}

    // Advices

    @Around("target_EntityRepository() && within_TemplateRepositorySubclasses() && deleteByUsernameIfExists()")
    public boolean around_deleteByUsernameIfExists(ProceedingJoinPoint jp) throws Throwable
    {
        return around_delete(jp);
    }
}
