package com.crm.gym.api.dtos.mappers.interfaces;

import com.crm.gym.api.dtos.trainer.TrainerModificationEmbeddedRequest;
import com.crm.gym.api.dtos.trainer.TrainerRegistrationRequest;
import com.crm.gym.api.dtos.trainer.TrainerModificationRequest;
import com.crm.gym.api.dtos.trainer.TrainerBriefProfile;
import com.crm.gym.api.dtos.trainer.TrainerCredentials;
import com.crm.gym.api.dtos.trainer.TrainerProfile;
import com.crm.gym.api.dtos.trainer.TrainerRef;
import com.crm.gym.api.entities.Trainer;

public interface TrainerMapper
{
    Trainer toEntity(TrainerRegistrationRequest dto);
    Trainer toEntity(TrainerModificationRequest dto);
    Trainer toEntity(TrainerModificationEmbeddedRequest dto);

    TrainerCredentials toCredentialsDto(Trainer trainer);
    TrainerProfile toProfileDto(Trainer trainer);
    TrainerBriefProfile toBriefProfileDto(Trainer trainer);
    TrainerRef toRefDto(Trainer trainer);
}
