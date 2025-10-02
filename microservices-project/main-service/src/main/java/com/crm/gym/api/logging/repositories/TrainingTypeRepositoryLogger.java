package com.crm.gym.api.logging.repositories;

import com.crm.gym.api.entities.TrainingType;
import com.crm.gym.api.repositories.interfaces.TrainingTypeRepository;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
public class TrainingTypeRepositoryLogger extends TemplateRepositoryLogger<UUID, TrainingType>
{
    public TrainingTypeRepositoryLogger()
    {
        super(LoggerFactory.getLogger(TrainingTypeRepository.class));
    }

    @Override
    protected Class<TrainingType> getEntityClass() {return TrainingType.class;}

    @Override
    @Pointcut("target(com.crm.gym.repositories.interfaces.TrainingTypeRepository)")
    public void target_EntityRepository() {}
}
