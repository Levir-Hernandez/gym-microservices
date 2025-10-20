package com.crm.gym.api.controllers;

import com.crm.gym.api.dtos.ActionType;
import com.crm.gym.api.dtos.TrainerWorkloadRequest;
import com.crm.gym.api.entities.TrainerWorkloadSummary;
import com.crm.gym.api.services.TrainerWorkloadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/trainers")
@Tag(name = "Trainers' Workloads", description = "Operations related to trainers' workloads")
public class TrainerWorkloadController
{
    private TrainerWorkloadService trainerWorkloadService;

    public TrainerWorkloadController(TrainerWorkloadService trainerWorkloadService)
    {
        this.trainerWorkloadService = trainerWorkloadService;
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
        return ResponseEntity.ok(trainerWorkloadService.getAllTrainersWorkloads());
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
            @Parameter(description = "Trainer's username", required = true)
            @PathVariable String trainerUsername
    )
    {
        TrainerWorkloadSummary workload = trainerWorkloadService.getTrainerWorkloadByUsername(trainerUsername);

        if(Objects.isNull(workload)) {return ResponseEntity.notFound().build();}
        else {return ResponseEntity.ok(workload);}
    }

    // +. Update Trainer Workload Summary
    @Operation(summary = "Update trainer workload summary", security = @SecurityRequirement(name = "user_auth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true, description = "Trainer workload update request",
                    content = @Content(schema = @Schema(implementation = TrainerWorkloadRequest.class))
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Trainer workload summary updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TrainerWorkloadSummary.class)
                    )
            )
    })
    @PostMapping("/workloads")
    public ResponseEntity<Void> updateTrainerWorkload(@RequestBody @Valid TrainerWorkloadRequest trainerWorkloadRequest)
    {
        switch (trainerWorkloadRequest.getActionType())
        {
            case ActionType.ADD -> trainerWorkloadService.increaseTrainerWorkload(
                    trainerWorkloadRequest.getTrainerUsername(),
                    trainerWorkloadRequest.getTrainerFirstname(),
                    trainerWorkloadRequest.getTrainerLastname(),
                    trainerWorkloadRequest.getTrainerStatus(),
                    trainerWorkloadRequest.getTrainingDate(),
                    trainerWorkloadRequest.getTrainingDuration()
            );
            case ActionType.DELETE -> trainerWorkloadService.decreaseTrainerWorkload(
                    trainerWorkloadRequest.getTrainerUsername(),
                    trainerWorkloadRequest.getTrainingDate(),
                    trainerWorkloadRequest.getTrainingDuration()
            );
        }
        return ResponseEntity.ok().build();
    }
}