package com.crm.gym.api.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class TrainerWorkloadSummary
{
    private String trainerUsername;
    private String trainerFirstname;
    private String trainerLastname;
    private Boolean trainerStatus;
    private Map<Integer, Map<Integer, Integer>> workloadSummary;

    public TrainerWorkloadSummary()
    {
        workloadSummary = new HashMap<>();
    }

    public TrainerWorkloadSummary(String trainerUsername, String trainerFirstname, String trainerLastname, Boolean trainerStatus)
    {
        this();
        this.trainerUsername = trainerUsername;
        this.trainerFirstname = trainerFirstname;
        this.trainerLastname = trainerLastname;
        this.trainerStatus = trainerStatus;
    }
}
