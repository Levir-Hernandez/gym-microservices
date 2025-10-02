package com.crm.gym.api.logging.services;

import com.crm.gym.api.entities.Trainee;
import com.crm.gym.api.services.TraineeService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TraineeServiceLogger extends UserServiceLogger<Trainee>
{
    public TraineeServiceLogger()
    {
        super(LoggerFactory.getLogger(TraineeService.class));
    }

    @Override
    protected Class<Trainee> getEntityClass() {return Trainee.class;}

    // Pointcuts

    @Override
    @Pointcut("target(com.crm.gym.api.services.TraineeService)")
    public void target_EntityService() {}

    @Pointcut("execution(boolean deleteTraineeByUsername(String))")
    public void deleteTraineeByUsername() {}

    // Advices

    @Override
    @Around("target_EntityService() && within_TemplateServiceSubclasses() && updateEntity()")
    public Trainee around_updateEntity(ProceedingJoinPoint jp) throws Throwable
    {
        return super.around_updateEntity(jp);
    }

    @Override
    @Around("target_EntityService() && within_TemplateServiceSubclasses() && deleteEntity()")
    public boolean around_deleteEntity(ProceedingJoinPoint jp) throws Throwable
    {
        return super.around_deleteEntity(jp);
    }

    @Around("target_EntityService() && deleteTraineeByUsername()")
    public boolean around_deleteTraineeByUsername(ProceedingJoinPoint jp) throws Throwable
    {
        return super.around_deleteEntity(jp);
    }
}
