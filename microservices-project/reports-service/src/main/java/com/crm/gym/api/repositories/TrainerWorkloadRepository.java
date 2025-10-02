package com.crm.gym.api.repositories;

import com.crm.gym.api.entities.TrainerWorkloadSummary;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TrainerWorkloadRepository
{
    private Map<String, TrainerWorkloadSummary> entities;

    public TrainerWorkloadRepository()
    {
        entities = new HashMap<>();
    }

    public TrainerWorkloadSummary save(TrainerWorkloadSummary summary)
    {
        return entities.put(summary.getTrainerUsername(), summary);
    }

    public TrainerWorkloadSummary findByTrainerUsername(String trainerUsername)
    {
        return entities.get(trainerUsername);
    }

    public List<TrainerWorkloadSummary> findAll()
    {
        return new ArrayList<>(entities.values());
    }
}
