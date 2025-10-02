package com.crm.gym.api.dtos.mappers.implementations;

import com.crm.gym.api.dtos.trainer.TrainerModificationEmbeddedRequest;
import com.crm.gym.api.dtos.trainer.TrainerRegistrationRequest;
import com.crm.gym.api.dtos.trainer.TrainerModificationRequest;
import com.crm.gym.api.dtos.trainer.TrainerBriefProfile;
import com.crm.gym.api.dtos.trainer.TrainerCredentials;
import com.crm.gym.api.dtos.trainer.TrainerProfile;
import com.crm.gym.api.dtos.trainer.TrainerRef;
import com.crm.gym.api.entities.Trainer;

import com.crm.gym.api.dtos.mappers.interfaces.TraineeMapper;
import com.crm.gym.api.dtos.mappers.interfaces.TrainerMapper;

import java.util.Optional;
import java.util.stream.Collectors;
import com.crm.gym.api.entities.Training;
import com.crm.gym.api.entities.TrainingType;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Lazy;

@Component
public class TrainerMapperImpl implements TrainerMapper
{
    private TraineeMapper traineeMapper;

    public TrainerMapperImpl(@Lazy TraineeMapper traineeMapper)
    {
        this.traineeMapper = traineeMapper;
    }

    @Override
    public Trainer toEntity(TrainerRegistrationRequest dto)
    {
        Trainer trainer = new Trainer();
        trainer.setFirstname(dto.getFirstname());
        trainer.setLastname(dto.getLastname());
        trainer.setSpecialization(new TrainingType(dto.getSpecialization()));
        trainer.setIsActive(true);
        return trainer;
    }

    @Override
    public Trainer toEntity(TrainerModificationRequest dto)
    {
        Trainer trainer = toEntity((TrainerRegistrationRequest) dto);
        trainer.setIsActive(dto.getIsActive());
        return trainer;
    }

    @Override
    public Trainer toEntity(TrainerModificationEmbeddedRequest dto)
    {
        Trainer trainer = toEntity((TrainerModificationRequest) dto);
        trainer.setUsername(dto.getUsername());
        return trainer;
    }

    @Override
    public TrainerCredentials toCredentialsDto(Trainer trainer)
    {
        TrainerCredentials dto = new TrainerCredentials();
        dto.setUsername(trainer.getUsername());
        dto.setPassword(trainer.getPassword());
        return dto;
    }

    @Override
    public TrainerProfile toProfileDto(Trainer trainer)
    {
        TrainerProfile dto = new TrainerProfile();
        dto.setUsername(trainer.getUsername());
        dto.setFirstname(trainer.getFirstname());
        dto.setLastname(trainer.getLastname());
        dto.setSpecialization(safeSpecializationExtract(trainer));
        dto.setIsActive(trainer.getIsActive());
        dto.setTrainees(
                trainer.getTrainings().stream()
                        .map(Training::getTrainee)
                        .distinct()
                        .map(traineeMapper::toBriefProfileDto)
                        .collect(Collectors.toList())
        );
        return dto;
    }

    @Override
    public TrainerBriefProfile toBriefProfileDto(Trainer trainer)
    {
        TrainerBriefProfile dto = new TrainerBriefProfile();
        dto.setUsername(trainer.getUsername());
        dto.setFirstname(trainer.getFirstname());
        dto.setLastname(trainer.getLastname());
        dto.setSpecialization(safeSpecializationExtract(trainer));
        return dto;
    }

    @Override
    public TrainerRef toRefDto(Trainer trainer)
    {
        TrainerRef dto = new TrainerRef();
        dto.setUsername(trainer.getUsername());
        return dto;
    }

    private String safeSpecializationExtract(Trainer trainer)
    {
        return Optional.ofNullable(trainer.getSpecialization())
                .map(TrainingType::getName).orElse(null);
    }
}
