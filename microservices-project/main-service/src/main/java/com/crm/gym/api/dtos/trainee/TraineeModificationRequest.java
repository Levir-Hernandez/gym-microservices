package com.crm.gym.api.dtos.trainee;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

@Getter @Setter
public class TraineeModificationRequest extends TraineeRegistrationRequest
{
    @NotNull(message = "Trainee isActive is required")
    private Boolean isActive;
}
