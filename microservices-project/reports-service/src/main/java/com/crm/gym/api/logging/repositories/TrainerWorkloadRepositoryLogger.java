package com.crm.gym.api.logging.repositories;

import com.crm.gym.api.entities.TrainerWorkloadSummary;
import com.crm.gym.api.repositories.TrainerWorkloadRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@Component
public class TrainerWorkloadRepositoryLogger
{
    private final Logger logger;
    private final String TRAINER_WORKLOAD_SUMMARY = "com.crm.gym.api.entities.TrainerWorkloadSummary";

    public TrainerWorkloadRepositoryLogger()
    {
        logger = LoggerFactory.getLogger(TrainerWorkloadRepository.class);
    }

    @Pointcut("within(com.crm.gym.api.repositories.TrainerWorkloadRepository)")
    public void within_TrainerWorkloadRepository(){}

    @Pointcut("execution("+TRAINER_WORKLOAD_SUMMARY+" save("+TRAINER_WORKLOAD_SUMMARY+"))")
    public void save() {}

    @Pointcut("execution("+TRAINER_WORKLOAD_SUMMARY+" findByTrainerUsername(String))")
    public void findByTrainerUsername() {}

    @Around("within_TrainerWorkloadRepository() && save()")
    public TrainerWorkloadSummary around_save(ProceedingJoinPoint jp) throws Throwable
    {
        logger.info("Saving Trainer workload summary into database");
        TrainerWorkloadSummary trainerWorkloadSummary = (TrainerWorkloadSummary) jp.proceed();
        logger.info("Stored Trainer workload summary into database");

        return trainerWorkloadSummary;
    }

    @Around("within_TrainerWorkloadRepository() && findByTrainerUsername()")
    public TrainerWorkloadSummary around_findByTrainerUsername(ProceedingJoinPoint jp) throws Throwable
    {
        Object[] args = jp.getArgs();
        Object trainerUsername = args[0];

        TrainerWorkloadSummary trainerWorkloadSummary = (TrainerWorkloadSummary) jp.proceed();
        if(Objects.nonNull(trainerWorkloadSummary))
        {
            logger.info("Fetching Trainer {} workload summary from database", trainerUsername);
        }
        else
        {
            logger.warn("Trainer {} workload summary not found. Returning null", trainerUsername);
        }
        return trainerWorkloadSummary;
    }
}
