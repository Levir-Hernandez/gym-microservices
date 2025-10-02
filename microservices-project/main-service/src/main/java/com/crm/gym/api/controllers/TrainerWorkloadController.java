package com.crm.gym.api.controllers;

import com.crm.gym.client.reports.TrainerWorkloadClient;
import com.crm.gym.client.reports.dtos.TrainerWorkloadSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/trainers")
@Tag(name = "Trainers' Workloads", description = "Operations related to trainers' workloads")
public class TrainerWorkloadController
{
    private TrainerWorkloadClient trainerWorkloadClient;

    public TrainerWorkloadController(TrainerWorkloadClient trainerWorkloadClient)
    {
        this.trainerWorkloadClient = trainerWorkloadClient;
    }

    // +. Get Trainer Workload Summary
    @Operation(summary = "Get trainer workload summary", security = @SecurityRequirement(name = "user_auth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Trainer workload summary retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TrainerWorkloadSummary.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Trainer workload summary not found", content = @Content)
    })
    @GetMapping("/{trainerUsername}/workloads")
    public ResponseEntity<TrainerWorkloadSummary> getTrainerWorkloadByUsername(
            @PathVariable String trainerUsername
    )
    {
        return trainerWorkloadClient.getTrainerWorkloadByUsername(trainerUsername);
    }

    // +. Get all Trainers' Workload summaries
    @Operation(summary = "Get all trainers' workloads", security = @SecurityRequirement(name = "user_auth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TrainerWorkloadSummary.class))
                    )
            )
    })
    @GetMapping("/workloads")
    public ResponseEntity<List<TrainerWorkloadSummary>> getAllTrainersWorkloads()
    {
        return trainerWorkloadClient.getAllTrainersWorkloads();
    }
}
