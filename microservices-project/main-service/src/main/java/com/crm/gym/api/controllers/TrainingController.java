package com.crm.gym.api.controllers;

import com.crm.gym.api.controllers.exceptions.ResourceNotFoundException;
import com.crm.gym.api.dtos.assemblers.TrainingModelAssembler;
import com.crm.gym.api.dtos.mappers.interfaces.TrainingMapper;
import com.crm.gym.api.dtos.training.TrainingDetails;
import com.crm.gym.api.dtos.training.TrainingRespDto;
import com.crm.gym.api.dtos.training.TrainingScheduleRequest;
import com.crm.gym.api.entities.Training;
import com.crm.gym.api.repositories.TrainingQueryCriteria;
import com.crm.gym.api.services.TrainingService;
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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@Tag(name = "Trainings", description = "Operations related to training sessions")
public class TrainingController
{
    private TrainingService trainingService;
    private TrainingMapper trainingMapper;
    private TrainingModelAssembler assembler;
    private PagedResourcesAssembler<TrainingRespDto> pagedAssembler;

    public TrainingController(TrainingService trainingService, TrainingMapper trainingMapper, TrainingModelAssembler assembler, PagedResourcesAssembler<TrainingRespDto> pagedAssembler)
    {
        this.trainingService = trainingService;
        this.trainingMapper = trainingMapper;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
    }

    // 14. Add Training
    @Operation(
            summary = "Add a new training session", security = @SecurityRequirement(name = "user_auth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Training data", required = true,
                    content = @Content(schema = @Schema(implementation = TrainingScheduleRequest.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Training created successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid training data provided", content = @Content)
    })
    @PostMapping("/trainings")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<TrainingRespDto> createTraining(@RequestBody @Valid TrainingScheduleRequest trainingDto)
    {
        Training training = trainingMapper.toEntity(trainingDto);
        training = trainingService.saveEntity(training);
        return assembler.toModel(trainingMapper.toDetailsDto(training));
    }

    // +. Delete Training Session
    @Operation(summary = "Delete training session", security = @SecurityRequirement(name = "user_auth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Training deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Training not found", content = @Content)
    })
    @DeleteMapping("/trainings/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteTrainingByName(
            @Parameter(description = "Training's name", required = true)
            @PathVariable String name
    )
    {
        boolean deleted = trainingService.deleteTrainingByName(name);
        if(!deleted){throw new ResourceNotFoundException();}

        return ResponseEntity.noContent()
                .header("X-Resource-Id", name)
                .build();
    }

    // +. Get all Trainings
    @Operation(summary = "Get all trainings", security = @SecurityRequirement(name = "user_auth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TrainingDetails.class))
                    )
            )
    })
    @GetMapping("/trainings")
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<EntityModel<TrainingRespDto>> getAllTrainings(
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(30) Integer size
    )
    {
        Pageable pageable = PageRequest.of(page, size);
        return pagedAssembler.toModel(
                trainingService.getAllEntities(pageable)
                        .map(trainingMapper::toDetailsDto)
        );
    }

    // 12. Get Trainee Trainings List
    @Operation(summary = "Get trainings for a trainee", security = @SecurityRequirement(name = "user_auth"),
            description = "Returns a list of trainings for a specific trainee with optional filters")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TrainingDetails.class))
                    )
            )
    })
    @GetMapping("/trainees/{traineeUsername}/trainings")
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<EntityModel<TrainingRespDto>> getTrainingsByTraineeUsernameAndCriteria(
            @Parameter(description = "Filter by trainee's username", required = true)
            @PathVariable String traineeUsername,

            @Parameter(description = "Filter by trainer's username")
            @RequestParam(required = false) String trainerUsername,

            @Parameter(description = "Filter trainings from this start date", example = "2025-01-01")
            @RequestParam(required = false) LocalDate fromDate,

            @Parameter(description = "Filter trainings until this end date", example = "2025-12-31")
            @RequestParam(required = false) LocalDate toDate,

            @Parameter(description = "Filter by training type name", example = "Fitness")
            @RequestParam(required = false) String trainingTypeName,

            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(30) Integer size)
    {
        Pageable pageable = PageRequest.of(page, size);
        TrainingQueryCriteria criteria = TrainingQueryCriteria.builder()
                .traineeUsername(traineeUsername)
                .trainerUsername(trainerUsername)
                .fromDate(fromDate)
                .toDate(toDate)
                .trainingTypeName(trainingTypeName)
                .build();
        return pagedAssembler.toModel(
                trainingService.getTrainingsByCriteria(criteria, pageable)
                        .map(trainingMapper::toDetailsDto)
        );
    }

    // 13. Get Trainer Trainings List
    @Operation(summary = "Get trainings for a trainer", security = @SecurityRequirement(name = "user_auth"),
            description = "Returns a list of trainings for a specific trainer with optional filters")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TrainingDetails.class))
                    )
            )
    })
    @GetMapping("/trainers/{trainerUsername}/trainings")
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<EntityModel<TrainingRespDto>> getTrainingsByTrainerUsernameAndCriteria(
            @Parameter(description = "Filter by trainer's username", required = true)
            @PathVariable String trainerUsername,

            @Parameter(description = "Filter by trainee's username")
            @RequestParam(required = false) String traineeUsername,

            @Parameter(description = "Filter trainings from this start date", example = "2025-01-01")
            @RequestParam(required = false) LocalDate fromDate,

            @Parameter(description = "Filter trainings until this end date", example = "2025-12-31")
            @RequestParam(required = false) LocalDate toDate,

            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(30) Integer size)
    {
        Pageable pageable = PageRequest.of(page, size);
        TrainingQueryCriteria criteria = TrainingQueryCriteria.builder()
                .trainerUsername(trainerUsername)
                .traineeUsername(traineeUsername)
                .fromDate(fromDate)
                .toDate(toDate)
                .build();
        return pagedAssembler.toModel(
                trainingService.getTrainingsByCriteria(criteria, pageable)
                        .map(trainingMapper::toDetailsDto)
        );
    }
}
