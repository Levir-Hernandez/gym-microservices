package com.crm.gym.api.dtos.trainer;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

@Getter @Setter
public class TrainerModificationEmbeddedRequest extends TrainerModificationRequest
{
    @NotNull(message = "Trainer username is required")
    private String username; // For identification purposes
}
