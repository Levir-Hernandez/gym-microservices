package com.crm.gym.client.reports.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class TrainerWorkloadRequest
{
    private String trainerUsername;
    private String trainerFirstname;
    private String trainerLastname;
    private Boolean trainerStatus;

    private LocalDate trainingDate;
    private Integer trainingDuration;

    private ActionType actionType;
}
