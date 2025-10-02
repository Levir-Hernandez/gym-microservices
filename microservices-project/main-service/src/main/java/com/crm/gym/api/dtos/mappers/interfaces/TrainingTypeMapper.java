package com.crm.gym.api.dtos.mappers.interfaces;

import com.crm.gym.api.entities.TrainingType;
import com.crm.gym.api.dtos.trainingType.TrainingTypeDto;

public interface TrainingTypeMapper
{
    TrainingTypeDto toDto(TrainingType trainingType);
}
