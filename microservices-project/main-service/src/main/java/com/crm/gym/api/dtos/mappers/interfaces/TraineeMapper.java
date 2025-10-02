package com.crm.gym.api.dtos.mappers.interfaces;

import com.crm.gym.api.dtos.trainee.TraineeRegistrationRequest;
import com.crm.gym.api.dtos.trainee.TraineeModificationRequest;
import com.crm.gym.api.dtos.trainee.TraineeBriefProfile;
import com.crm.gym.api.dtos.trainee.TraineeCredentials;
import com.crm.gym.api.dtos.trainee.TraineeProfile;
import com.crm.gym.api.dtos.trainee.TraineeRef;
import com.crm.gym.api.entities.Trainee;

public interface TraineeMapper
{
    Trainee toEntity(TraineeRegistrationRequest dto);
    Trainee toEntity(TraineeModificationRequest dto);

    TraineeCredentials toCredentialsDto(Trainee trainee);
    TraineeProfile toProfileDto(Trainee trainee);
    TraineeBriefProfile toBriefProfileDto(Trainee trainee);
    TraineeRef toRefDto(Trainee trainee);
}
