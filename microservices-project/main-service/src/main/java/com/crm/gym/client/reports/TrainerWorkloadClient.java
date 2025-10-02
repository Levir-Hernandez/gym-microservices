package com.crm.gym.client.reports;

import com.crm.gym.client.reports.config.FeignClientConfig;
import com.crm.gym.client.reports.dtos.TrainerWorkloadRequest;
import com.crm.gym.client.reports.dtos.TrainerWorkloadSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "reports-service",
        fallback = TrainerWorkloadFallback.class,
        configuration = FeignClientConfig.class
)
public interface TrainerWorkloadClient
{
    @GetMapping("/trainers/{trainerUsername}/workloads")
    ResponseEntity<TrainerWorkloadSummary> getTrainerWorkloadByUsername(@PathVariable String trainerUsername);

    @GetMapping("/trainers/workloads")
    ResponseEntity<List<TrainerWorkloadSummary>> getAllTrainersWorkloads();

    @PostMapping("/trainers/workloads")
    ResponseEntity<Void> updateTrainerWorkload(@RequestBody TrainerWorkloadRequest trainerWorkloadRequest);
}
