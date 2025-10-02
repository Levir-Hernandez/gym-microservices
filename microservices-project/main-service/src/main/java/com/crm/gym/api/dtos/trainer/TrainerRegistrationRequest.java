package com.crm.gym.api.dtos.trainer;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

@Getter @Setter
public class TrainerRegistrationRequest
{
    @NotNull(message = "Trainer firstname is required")
    private String firstname;

    @NotNull(message = "Trainer lastname is required")
    private String lastname;

    @NotNull(message = "Trainer specialization is required")
    private String specialization;
}
