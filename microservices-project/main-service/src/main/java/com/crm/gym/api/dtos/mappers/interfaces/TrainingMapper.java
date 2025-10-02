package com.crm.gym.api.dtos.mappers.interfaces;

import com.crm.gym.api.entities.Training;
import com.crm.gym.api.dtos.training.TrainingDetails;
import com.crm.gym.api.dtos.training.TrainingScheduleRequest;

public interface TrainingMapper
{
    Training toEntity(TrainingScheduleRequest dto);
    TrainingDetails toDetailsDto(Training training);
}
