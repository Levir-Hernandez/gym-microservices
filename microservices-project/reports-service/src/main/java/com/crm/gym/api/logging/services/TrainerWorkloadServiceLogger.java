package com.crm.gym.api.logging.services;

import com.crm.gym.api.entities.TrainerWorkloadSummary;
import com.crm.gym.api.services.TrainerWorkloadService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Objects;

@Aspect
@Component
public class TrainerWorkloadServiceLogger
{
    private final Logger logger;
    private final String TRAINER_WORKLOAD_SUMMARY = "com.crm.gym.api.entities.TrainerWorkloadSummary";

    public TrainerWorkloadServiceLogger()
    {
        logger = LoggerFactory.getLogger(TrainerWorkloadService.class);
    }

    @Pointcut("within(com.crm.gym.api.services.TrainerWorkloadService)")
    public void within_TrainerWorkloadService(){}

    @Pointcut("execution("+TRAINER_WORKLOAD_SUMMARY+" getTrainerWorkloadByUsername(..))")
    public void getTrainerWorkloadByUsername() {}

    @Pointcut("execution("+TRAINER_WORKLOAD_SUMMARY+" increaseTrainerWorkload(..))")
    public void increaseTrainerWorkload() {}

    @Pointcut("execution("+TRAINER_WORKLOAD_SUMMARY+" decreaseTrainerWorkload(..))")
    public void decreaseTrainerWorkload() {}

    @Around("within_TrainerWorkloadService() && getTrainerWorkloadByUsername()")
    public TrainerWorkloadSummary around_getTrainerWorkloadByUsername(ProceedingJoinPoint jp) throws Throwable
    {
        Object[] args = jp.getArgs();
        Object trainerUsername = args[0];

        logger.info("Searching Trainer {} workload summary", trainerUsername);

        TrainerWorkloadSummary trainerWorkloadSummary = (TrainerWorkloadSummary) jp.proceed(args);
        if(Objects.nonNull(trainerWorkloadSummary))
        {
            logger.info("Trainer {} workload summary retrieved", trainerUsername);
        }

        return trainerWorkloadSummary;
    }

    @Around("within_TrainerWorkloadService() && increaseTrainerWorkload()")
    public TrainerWorkloadSummary around_increaseTrainerWorkload(ProceedingJoinPoint jp) throws Throwable
    {
        Object[] args = jp.getArgs();

        String trainerUsername = (String) args[0];
        LocalDate trainingDate = (LocalDate) args[4];
        Integer duration = (Integer) args[5];

        logger.info("Increasing workload for Trainer {}", trainerUsername);
        TrainerWorkloadSummary trainerWorkloadSummary = (TrainerWorkloadSummary) jp.proceed(args);
        logger.info("Increased workload for Trainer {} on {} by {} minutes", trainerUsername, trainingDate, duration);

        return trainerWorkloadSummary;
    }

    @Around("within_TrainerWorkloadService() && decreaseTrainerWorkload()")
    public TrainerWorkloadSummary around_decreaseTrainerWorkload(ProceedingJoinPoint jp) throws Throwable
    {
        Object[] args = jp.getArgs();

        String trainerUsername = (String) args[0];
        LocalDate trainingDate = (LocalDate) args[1];
        Integer duration = (Integer) args[2];

        logger.info("Decreasing workload for Trainer {}", trainerUsername);

        TrainerWorkloadSummary trainerWorkloadSummary = (TrainerWorkloadSummary) jp.proceed(args);

        if(Objects.nonNull(trainerWorkloadSummary))
        {
            logger.info("Decreased workload for Trainer {} on {} by {} minutes", trainerUsername, trainingDate, duration);
        }
        else
        {
            logger.warn("Trainer {} workload not found. Decrease skipped. Returning null", trainerUsername);
        }

        return trainerWorkloadSummary;
    }
}
