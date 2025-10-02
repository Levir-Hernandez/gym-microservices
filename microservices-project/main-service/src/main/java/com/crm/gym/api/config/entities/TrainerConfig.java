package com.crm.gym.api.config.entities;

import com.crm.gym.api.services.TrainerService;
import com.crm.gym.api.util.EntityResourceLoader;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;

import com.crm.gym.api.repositories.interfaces.TrainerRepository;
import com.crm.gym.api.entities.Trainer;
import org.springframework.core.annotation.Order;

import java.util.UUID;

@Configuration
@DependsOn("trainingTypeConfig")
public class TrainerConfig extends TemplateConfig<UUID, Trainer, TrainerRepository>
{
    public TrainerConfig(@Value("${storage.trainers.path:}") String trainersPath,
                         TrainerService trainerService,
                         EntityResourceLoader entityResourceLoader)
    {
        super(trainersPath, trainerService, entityResourceLoader);
    }

    @Override
    protected Class<Trainer> getEntityClass() {return Trainer.class;}

    @Order(3)
    @Override
    protected boolean createEntitiesFromJson() {return super.createEntitiesFromJson();}
}
