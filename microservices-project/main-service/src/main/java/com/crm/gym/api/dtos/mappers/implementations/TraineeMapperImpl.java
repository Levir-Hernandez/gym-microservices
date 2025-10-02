package com.crm.gym.api.dtos.mappers.implementations;

import com.crm.gym.api.dtos.trainee.TraineeRegistrationRequest;
import com.crm.gym.api.dtos.trainee.TraineeModificationRequest;
import com.crm.gym.api.dtos.trainee.TraineeBriefProfile;
import com.crm.gym.api.dtos.trainee.TraineeCredentials;
import com.crm.gym.api.dtos.trainee.TraineeProfile;
import com.crm.gym.api.dtos.trainee.TraineeRef;
import com.crm.gym.api.entities.Trainee;

import java.util.stream.Collectors;
import com.crm.gym.api.entities.Training;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Lazy;
import com.crm.gym.api.dtos.mappers.interfaces.TraineeMapper;
import com.crm.gym.api.dtos.mappers.interfaces.TrainerMapper;

@Component
public class TraineeMapperImpl implements TraineeMapper
{
    private TrainerMapper trainerMapper;

    public TraineeMapperImpl(@Lazy TrainerMapper trainerMapper)
    {
        this.trainerMapper = trainerMapper;
    }

    public Trainee toEntity(TraineeRegistrationRequest dto)
    {
        Trainee trainee = new Trainee();
        trainee.setFirstname(dto.getFirstname());
        trainee.setLastname(dto.getLastname());
        trainee.setBirthdate(dto.getBirthdate());
        trainee.setAddress(dto.getAddress());
        trainee.setIsActive(true);
        return trainee;
    }

    public Trainee toEntity(TraineeModificationRequest dto)
    {
        Trainee trainee = toEntity((TraineeRegistrationRequest) dto);
        trainee.setIsActive(dto.getIsActive());
        return trainee;
    }

    public TraineeCredentials toCredentialsDto(Trainee trainee)
    {
        TraineeCredentials dto = new TraineeCredentials();
        dto.setUsername(trainee.getUsername());
        dto.setPassword(trainee.getPassword());
        return dto;
    }

    public TraineeProfile toProfileDto(Trainee trainee)
    {
        TraineeProfile dto = new TraineeProfile();
        dto.setUsername(trainee.getUsername());
        dto.setFirstname(trainee.getFirstname());
        dto.setLastname(trainee.getLastname());
        dto.setBirthdate(trainee.getBirthdate());
        dto.setAddress(trainee.getAddress());
        dto.setIsActive(trainee.getIsActive());
        dto.setTrainers(
                trainee.getTrainings().stream()
                        .map(Training::getTrainer)
                        .distinct()
                        .map(trainerMapper::toBriefProfileDto)
                        .collect(Collectors.toList())
        );
        return dto;
    }

    @Override
    public TraineeBriefProfile toBriefProfileDto(Trainee trainee)
    {
        TraineeBriefProfile dto = new TraineeBriefProfile();
        dto.setUsername(trainee.getUsername());
        dto.setFirstname(trainee.getFirstname());
        dto.setLastname(trainee.getLastname());
        return dto;
    }

    @Override
    public TraineeRef toRefDto(Trainee trainee)
    {
        TraineeRef dto = new TraineeRef();
        dto.setUsername(trainee.getUsername());
        return dto;
    }
}