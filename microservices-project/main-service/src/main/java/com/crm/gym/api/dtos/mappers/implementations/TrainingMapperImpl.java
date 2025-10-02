package com.crm.gym.api.dtos.mappers.implementations;

import com.crm.gym.api.entities.Trainee;
import com.crm.gym.api.entities.Trainer;
import com.crm.gym.api.entities.Training;
import com.crm.gym.api.entities.TrainingType;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.crm.gym.api.dtos.training.TrainingDetails;
import com.crm.gym.api.dtos.training.TrainingScheduleRequest;
import com.crm.gym.api.dtos.mappers.interfaces.TrainingMapper;

@Component
public class TrainingMapperImpl implements TrainingMapper
{
    @Override
    public Training toEntity(TrainingScheduleRequest dto)
    {
        Training training = new Training();
        training.setTrainingType(new TrainingType(dto.getTrainingType()));
        training.setName(dto.getName());
        training.setDate(dto.getDate());
        training.setDuration(dto.getDuration());
        training.setTrainer(new Trainer(dto.getTrainerUsername()));
        training.setTrainee(new Trainee(dto.getTraineeUsername()));
        return training;
    }

    @Override
    public TrainingDetails toDetailsDto(Training training)
    {
        TrainingDetails dto = new TrainingDetails();
        dto.setTrainingType(safeFieldExtract(training.getTrainingType(), TrainingType::getName));
        dto.setName(training.getName());
        dto.setDate(training.getDate());
        dto.setDuration(training.getDuration());
        dto.setTrainerUsername(safeFieldExtract(training.getTrainer(), Trainer::getUsername));
        dto.setTraineeUsername(safeFieldExtract(training.getTrainee(), Trainee::getUsername));
        return dto;
    }

    private <T, R> R safeFieldExtract(T field, Function<T, R> keyExtractor)
    {
        return Optional.ofNullable(field).map(keyExtractor).orElse(null);
    }
}
