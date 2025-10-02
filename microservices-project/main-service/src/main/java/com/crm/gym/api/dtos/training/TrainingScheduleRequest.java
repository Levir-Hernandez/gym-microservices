package com.crm.gym.api.dtos.training;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class TrainingScheduleRequest implements TrainingRespDto
{
    @NotNull(message = "Training name is required")
    private String name;

    @NotNull(message = "Training type is required")
    private String trainingType;

    @NotNull(message = "Training date is required")
    private LocalDate date;

    @NotNull(message = "Training duration is required")
    private Integer duration;

    @NotNull(message = "Trainer username is required")
    private String trainerUsername;

    @NotNull(message = "Trainee username is required")
    private String traineeUsername;
}
