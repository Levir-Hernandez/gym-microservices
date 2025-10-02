package com.crm.gym.api.logging.services;

import com.crm.gym.api.entities.Training;
import com.crm.gym.api.services.TrainingService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.ToIntFunction;

@Aspect
@Component
public class TrainingServiceLogger extends TemplateServiceLogger<UUID, Training>
{
    private final String LIST = "java.util.List";
    private final String TRAINING_QUERY_CRITERIA = "com.crm.gym.repositories.TrainingQueryCriteria";

    private final String PAGE = "org.springframework.data.domain.Page";
    private final String PAGEABLE = "org.springframework.data.domain.Pageable";

    public TrainingServiceLogger()
    {
        super(LoggerFactory.getLogger(TrainingService.class));
    }

    @Override
    protected Class<Training> getEntityClass() {return Training.class;}

    // Pointcuts

    @Override
    @Pointcut("target(com.crm.gym.api.services.TrainingService)")
    public void target_EntityService() {}

    @Pointcut("execution("+LIST+" getTrainingsByCriteria("+TRAINING_QUERY_CRITERIA+"))")
    public void getTrainingsByCriteria() {}

    @Pointcut("execution("+PAGE+" getTrainingsByCriteria("+TRAINING_QUERY_CRITERIA+","+PAGEABLE+"))")
    public void getPagedTrainingsByCriteria() {}

    // Advices

    @Around("target_EntityService() && getTrainingsByCriteria()")
    public List<Training> around_getTrainingsByCriteria(ProceedingJoinPoint jp) throws Throwable
    {
        return around_getTrainingsByCriteriaTemplate(jp, List::size);
    }

    @Around("target_EntityService() && getPagedTrainingsByCriteria()")
    public Page<Training> around_getPagedTrainingsByCriteria(ProceedingJoinPoint jp) throws Throwable
    {
        return around_getTrainingsByCriteriaTemplate(
                jp, pagedTrainings -> (int) pagedTrainings.getTotalElements()
        );
    }

    private <T> T around_getTrainingsByCriteriaTemplate(ProceedingJoinPoint jp, ToIntFunction<T> sizeExtractor) throws Throwable
    {
        logger.info("Searching for trainings that match the given criteria");

        T trainings = (T) jp.proceed();
        if(Objects.nonNull(trainings))
        {
            logger.info("Found {} training(s) matching the criteria", sizeExtractor.applyAsInt(trainings));
        }

        return trainings;
    }
}
