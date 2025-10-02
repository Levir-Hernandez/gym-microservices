package com.crm.gym.api.repositories;

import lombok.Getter;
import lombok.Builder;

import java.time.LocalDate;

@Getter
@Builder
public class TrainingQueryCriteria
{
    private String traineeUsername;
    private String trainerUsername;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String trainingTypeName;
}
