package com.crm.gym.api.entities;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@Document("trainer_workload_summaries")
@CompoundIndex(name = "trainer_fullname_index", def = "{'trainerFirstname':1, 'trainerLastname':1}")
public class TrainerWorkloadSummary
{
    @Id
    private String trainerUsername;
    private String trainerFirstname;
    private String trainerLastname;
    private Boolean trainerStatus;
    private Map<Integer, Map<Integer, Integer>> workloadSummary;

    public TrainerWorkloadSummary()
    {
        workloadSummary = new HashMap<>();
    }

    public TrainerWorkloadSummary(String trainerUsername)
    {
        this();
        this.trainerUsername = trainerUsername;
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
