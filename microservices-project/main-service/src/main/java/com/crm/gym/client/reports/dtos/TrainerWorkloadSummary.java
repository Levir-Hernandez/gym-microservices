package com.crm.gym.client.reports.dtos;

import java.util.Map;

public class TrainerWorkloadSummary
{
    private String trainerUsername;
    private String trainerFirstname;
    private String trainerLastname;
    private Boolean trainerStatus;
    private Map<Integer, Map<Integer, Integer>> workloadSummary;
}
