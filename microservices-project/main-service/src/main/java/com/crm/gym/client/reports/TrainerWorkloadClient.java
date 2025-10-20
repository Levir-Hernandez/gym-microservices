package com.crm.gym.client.reports;

import com.crm.gym.client.reports.dtos.TrainerWorkloadRequest;
import com.crm.gym.client.reports.dtos.TrainerWorkloadSummary;
import org.springframework.http.ResponseEntity;
import java.util.List;

public interface TrainerWorkloadClient extends TrainerWorkloadFallback
{
    ResponseEntity<TrainerWorkloadSummary> getTrainerWorkloadByUsername(String trainerUsername);
    ResponseEntity<List<TrainerWorkloadSummary>> getAllTrainersWorkloads();
    ResponseEntity<Void> updateTrainerWorkload(TrainerWorkloadRequest trainerWorkloadRequest);
}
