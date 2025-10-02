package com.crm.gym.api.config.entities;

import com.crm.gym.api.services.TrainingService;
import com.crm.gym.api.util.EntityResourceLoader;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;

import com.crm.gym.api.repositories.interfaces.TrainingRepository;
import com.crm.gym.api.entities.Training;
import org.springframework.core.annotation.Order;

import java.util.UUID;

@Configuration
@DependsOn({"traineeConfig", "trainerConfig", "trainingTypeConfig"})
public class TrainingConfig extends TemplateConfig<UUID, Training, TrainingRepository>
{
    public TrainingConfig(@Value("${storage.trainings.path:}") String trainingsPath,
                          TrainingService trainingService,
                          EntityResourceLoader entityResourceLoader)
    {
        super(trainingsPath, trainingService, entityResourceLoader);
    }

    @Override
    protected Class<Training> getEntityClass() {return Training.class;}

    @Order(4)
    @Override
    protected boolean createEntitiesFromJson() {return super.createEntitiesFromJson();}
}
