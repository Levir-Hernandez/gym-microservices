package com.crm.gym.client.reports;

import java.util.List;
import org.springframework.http.ResponseEntity;
import com.crm.gym.client.reports.dtos.TrainerWorkloadRequest;
import com.crm.gym.client.reports.dtos.TrainerWorkloadSummary;

import org.springframework.stereotype.Component;

@Component
public class TrainerWorkloadFallback implements TrainerWorkloadClient
{
    @Override
    public ResponseEntity<List<TrainerWorkloadSummary>> getAllTrainersWorkloads()
    {
        return ResponseEntity.ok(List.of());
    }

    @Override
    public ResponseEntity<TrainerWorkloadSummary> getTrainerWorkloadByUsername(String trainerUsername)
    {
        return ResponseEntity.ok(new TrainerWorkloadSummary());
    }

    @Override
    public ResponseEntity<Void> updateTrainerWorkload(TrainerWorkloadRequest trainerWorkloadRequest)
    {
        return ResponseEntity.ok().build();
    }
}
