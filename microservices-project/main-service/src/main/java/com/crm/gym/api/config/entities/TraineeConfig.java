package com.crm.gym.api.config.entities;

import com.crm.gym.api.services.TraineeService;
import com.crm.gym.api.util.EntityResourceLoader;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;

import com.crm.gym.api.repositories.interfaces.TraineeRepository;
import com.crm.gym.api.entities.Trainee;

import java.util.UUID;

@Configuration
public class TraineeConfig extends TemplateConfig<UUID, Trainee, TraineeRepository>
{
    public TraineeConfig(@Value("${storage.trainees.path:}") String traineesPath,
                         TraineeService traineeService,
                         EntityResourceLoader entityResourceLoader)
    {
        super(traineesPath, traineeService, entityResourceLoader);
    }

    @Override
    protected Class<Trainee> getEntityClass() {return Trainee.class;}

    @Order(2)
    @Override
    protected boolean createEntitiesFromJson() {return super.createEntitiesFromJson();}
}
