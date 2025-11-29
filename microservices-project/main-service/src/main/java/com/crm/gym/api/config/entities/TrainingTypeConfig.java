package com.crm.gym.api.config.entities;

import com.crm.gym.api.services.TrainingTypeService;
import com.crm.gym.api.util.EntityResourceLoader;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;

import com.crm.gym.api.repositories.interfaces.TrainingTypeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import com.crm.gym.api.entities.TrainingType;

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
        /*-------------------------------------------------------------------*
         * If entities already exist in the persistent volume, then a        *
         * ConstraintViolationException("training_types_name_idx") is thrown *
         *-------------------------------------------------------------------*/

        boolean createdFromJson;
        try {createdFromJson = super.createEntitiesFromJson();}
        catch (DataIntegrityViolationException e) {return false;}

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
