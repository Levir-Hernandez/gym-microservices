package com.crm.gym.api.dtos.mappers.implementations;

import com.crm.gym.api.entities.TrainingType;
import org.springframework.stereotype.Component;
import com.crm.gym.api.dtos.trainingType.TrainingTypeDto;
import com.crm.gym.api.dtos.mappers.interfaces.TrainingTypeMapper;

@Component
public class TrainingTypeMapperImpl implements TrainingTypeMapper
{
    @Override
    public TrainingTypeDto toDto(TrainingType trainingType)
    {
        TrainingTypeDto dto = new TrainingTypeDto();
        dto.setName(trainingType.getName());
        return dto;
    }
}
