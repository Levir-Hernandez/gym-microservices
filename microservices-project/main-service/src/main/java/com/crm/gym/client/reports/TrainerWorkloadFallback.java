package com.crm.gym.client.reports;

import java.util.List;
import org.springframework.http.ResponseEntity;
import com.crm.gym.client.reports.dtos.TrainerWorkloadRequest;
import com.crm.gym.client.reports.dtos.TrainerWorkloadSummary;

public interface TrainerWorkloadFallback
{
    default ResponseEntity<TrainerWorkloadSummary> fallbackForGetTrainerWorkloadByUsername(String trainerUsername, Throwable throwable)
    {
        return ResponseEntity.ok(new TrainerWorkloadSummary());
    }

    default ResponseEntity<List<TrainerWorkloadSummary>> fallbackForGetAllTrainersWorkloads(Throwable throwable)
    {
        return ResponseEntity.ok(List.of());
    }

    default ResponseEntity<Void> fallbackForUpdateTrainerWorkload(TrainerWorkloadRequest trainerWorkloadRequest, Throwable throwable)
    {
        return ResponseEntity.ok().build();
    }
}
