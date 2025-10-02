package com.crm.gym.api.services;

import com.crm.gym.api.entities.TrainingType;
import com.crm.gym.api.repositories.interfaces.TrainingTypeRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TrainingTypeService extends TemplateService<UUID, TrainingType, TrainingTypeRepository>
{
    public TrainingTypeService(TrainingTypeRepository repository)
    {
        super(repository);
    }
}
