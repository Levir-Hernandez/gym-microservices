package com.crm.gym.api.logging.services;

import com.crm.gym.api.entities.Trainer;
import com.crm.gym.api.services.TrainerService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.ToIntFunction;

@Aspect
@Component
    public class TrainerServiceLogger extends UserServiceLogger<Trainer>
{
    private final String SET = "java.util.Set";
    private final String LIST = "java.util.List";

    private final String PAGE = "org.springframework.data.domain.Page";
    private final String PAGEABLE = "org.springframework.data.domain.Pageable";

    public TrainerServiceLogger()
    {
        super(LoggerFactory.getLogger(TrainerService.class));
    }

    @Override
    protected Class<Trainer> getEntityClass() {return Trainer.class;}

    // Pointcuts

    @Override
    @Pointcut("target(com.crm.gym.api.services.TrainerService)")
    public void target_EntityService() {}

    @Pointcut("execution("+LIST+" getAllUnassignedForTraineeByUsername(String))")
    public void getAllUnassignedForTraineeByUsername() {}

    @Pointcut("execution("+PAGE+" getAllUnassignedForTraineeByUsername(String,"+PAGEABLE+"))")
    public void getAllPagedUnassignedForTraineeByUsername() {}

    @Pointcut("execution("+SET+" updateAssignedTrainersForTrainee(String,"+SET+"))")
    public void updateAssignedTrainersForTrainee() {}

    // Advices

    @Override
    @Around("target_EntityService() && within_TemplateServiceSubclasses() && updateEntity()")
    public Trainer around_updateEntity(ProceedingJoinPoint jp) throws Throwable
    {
        return super.around_updateEntity(jp);
    }

    @Around("target_EntityService() && getAllUnassignedForTraineeByUsername()")
    public List<Trainer> around_getAllUnassignedForTraineeByUsername(ProceedingJoinPoint jp) throws Throwable
    {
        return around_getAllUnassignedForTraineeByUsernameTemplate(jp, List::size);
    }

    @Around("target_EntityService() && getAllPagedUnassignedForTraineeByUsername()")
    public Page<Trainer> around_getAllPagedUnassignedForTraineeByUsername(ProceedingJoinPoint jp) throws Throwable
    {
        return around_getAllUnassignedForTraineeByUsernameTemplate(
                jp, pagedTrainings -> (int) pagedTrainings.getTotalElements()
        );
    }

    @Around("target_EntityService() && updateAssignedTrainersForTrainee()")
    public Set<Trainer> around_updateAssignedTrainersForTrainee(ProceedingJoinPoint jp) throws Throwable
    {
        Object[] args = jp.getArgs();
        String username = (String) args[0];
        Set<Trainer> trainers = (Set<Trainer>) args[1];

        logger.info("Updating {} trainers(s) for Trainee {}", trainers.size(), username);
        Set<Trainer> updatedTrainers = (Set<Trainer>) jp.proceed();

        if(Objects.nonNull(updatedTrainers))
        {
            logger.info("Updated {}/{} trainer(s) for Trainee {}", updatedTrainers.size(), trainers.size(), username);
        }
        else
        {
            logger.warn("Trainee {} not found. Bulk update skipped. Returning null", username);
        }

        return updatedTrainers;
    }

    private <T> T around_getAllUnassignedForTraineeByUsernameTemplate(ProceedingJoinPoint jp, ToIntFunction<T> sizeExtractor) throws Throwable
    {
        Object[] args = jp.getArgs();
        String username = (String) args[0];

        logger.info("Searching for trainers not assigned to Trainee {}", username);

        T unassignedTrainers = (T) jp.proceed();

        if(Objects.nonNull(unassignedTrainers))
        {
            logger.info("Found {} unassigned trainer(s) for Trainee {}", sizeExtractor.applyAsInt(unassignedTrainers), username);
        }
        else
        {
            logger.warn("Trainee {} not found. Returning null", username);
        }

        return unassignedTrainers;
    }
}
