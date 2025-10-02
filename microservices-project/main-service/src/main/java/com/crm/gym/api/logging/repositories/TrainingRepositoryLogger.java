package com.crm.gym.api.logging.repositories;

import com.crm.gym.api.entities.Training;
import com.crm.gym.api.repositories.interfaces.TrainingRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@Aspect
@Component
public class TrainingRepositoryLogger extends TemplateRepositoryLogger<UUID, Training>
{
    public TrainingRepositoryLogger()
    {
        super(LoggerFactory.getLogger(TrainingRepository.class));
    }

    @Override
    protected Class<Training> getEntityClass() {return Training.class;}

    // Pointcuts

    @Override
    @Pointcut("target(com.crm.gym.repositories.interfaces.TrainingRepository)")
    public void target_EntityRepository() {}

    @Pointcut("execution(* findByCriteria(..))")
    public void findByCriteria() {}

    // Advices

    @Override
    public Training around_create(ProceedingJoinPoint jp) throws Throwable
    {
        Object[] args = jp.getArgs();
        Training training = (Training) args[0];

        // Assumes the following behavior for foreign key references:
        // - null values remain null
        // - valid non-null references remain unchanged
        // - invalid non-null references are automatically set to null

        boolean hadReference = Stream.of(training.getTrainingType(), training.getTrainee(),
                training.getTrainer()).anyMatch(Objects::nonNull);

        training = super.around_create(jp);

        boolean referenceWasCleared = Stream.of(training.getTrainingType(), training.getTrainee(),
                training.getTrainer()).anyMatch(Objects::isNull);

        if(hadReference && referenceWasCleared)
        {
            logger.warn("Non-existent foreign key references detected. Replaced with null");
        }

        return training;
    }

    @Before("target_EntityRepository() && findByCriteria()")
    public void before_findByCriteria()
    {
        logger.info("Fetching trainings based on provided criteria");
    }
}
