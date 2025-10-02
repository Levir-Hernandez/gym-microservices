package com.crm.gym.api.repositories.implementations;

import com.crm.gym.api.entities.TrainingType;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class TrainingTypeRepositoryImpl extends TemplateRepositoryImpl<UUID, TrainingType>
{
    @Override
    protected Class<TrainingType> getEntityClass() {return TrainingType.class;}
}
