package com.crm.gym.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class TrainerWorkloadRequest
{
    @NotNull(message = "Trainer username is required")
    private String trainerUsername;

    private String trainerFirstname;
    private String trainerLastname;
    private Boolean trainerStatus;

    @NotNull(message = "Training date is required")
    private LocalDate trainingDate;

    @NotNull(message = "Training duration is required")
    private Integer trainingDuration;

    @NotNull(message = "Action type is required")
    private ActionType actionType;
}
