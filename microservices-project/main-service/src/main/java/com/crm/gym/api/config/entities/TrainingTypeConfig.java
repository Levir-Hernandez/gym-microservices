package com.crm.gym.api.config.entities;

import com.crm.gym.api.services.TrainingTypeService;
import com.crm.gym.api.util.EntityResourceLoader;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import com.crm.gym.api.repositories.interfaces.TrainingTypeRepository;
import com.crm.gym.api.entities.TrainingType;
import org.springframework.core.annotation.Order;

import java.util.UUID;

@Configuration
public class TrainingTypeConfig extends TemplateConfig<UUID, TrainingType, TrainingTypeRepository>
{
    public TrainingTypeConfig(@Value("${storage.training-types.path:}") String trainingTypesPath,
                              TrainingTypeService trainingTypeService,
                              EntityResourceLoader entityResourceLoader)
    {
        super(trainingTypesPath, trainingTypeService, entityResourceLoader);
    }

    @Override
    protected Class<TrainingType> getEntityClass() {return TrainingType.class;}

    @Order(1)
    @Override
    protected boolean createEntitiesFromJson()
    {
        boolean createdFromJson = super.createEntitiesFromJson();
        if(entityService.getEntitiesCount() < 1 && !createdFromJson)
        {
            throw new UnavailableTrainingTypesException();
        }
        return createdFromJson;
    }

    private static class UnavailableTrainingTypesException extends RuntimeException
    {
        public UnavailableTrainingTypesException()
        {
            super("TrainingTypes missing: none found in the database and no valid entity source provided");
        }
    }
}
