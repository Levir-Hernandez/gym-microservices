package com.crm.gym.api.controllers;

import com.crm.gym.api.controllers.exceptions.ResourceNotFoundException;
import com.crm.gym.api.dtos.assemblers.TrainerModelAssembler;
import com.crm.gym.api.dtos.mappers.interfaces.TrainerMapper;
import com.crm.gym.api.dtos.trainer.*;
import com.crm.gym.api.entities.Trainer;
import com.crm.gym.api.services.TrainerService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Trainers", description = "Operations related to trainers")
public class TrainerController extends UserController<TrainerService, TrainerRespDto>
{
    private TrainerService trainerService;
    private TrainerMapper trainerMapper;
    private TrainerModelAssembler assembler;
    private PagedResourcesAssembler<TrainerRespDto> pagedAssembler;
    private TrainerTokenWrapper.Builder trainerTokenWrapperBuilder;

    public TrainerController(TrainerService trainerService, TrainerMapper trainerMapper, TrainerModelAssembler assembler, PagedResourcesAssembler<TrainerRespDto> pagedAssembler, TrainerTokenWrapper.Builder trainerTokenWrapperBuilder)
    {
        super(trainerService, assembler, trainerTokenWrapperBuilder);

        this.trainerService = trainerService;
        this.trainerMapper = trainerMapper;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
        this.trainerTokenWrapperBuilder = trainerTokenWrapperBuilder;
    }

    @Override
    protected Supplier<TrainerRespDto> userRefSupplier() {return TrainerRef::new;}

    // 2. Trainer Registration
    @Operation(
            summary = "Register a new trainer",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Trainer data", required = true,
                    content = @Content(schema = @Schema(implementation = TrainerRegistrationRequest.class))
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201", description = "Trainer registered successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TrainerCredentials.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid trainer data provided", content = @Content)
    })
    @PostMapping("/trainers")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<TrainerRespDto> createTrainer(@RequestBody @Valid TrainerRegistrationRequest trainerDto)
    {
        Trainer trainer = trainerMapper.toEntity(trainerDto);
        trainer = trainerService.saveEntity(trainer);

        TrainerRespDto trainerRespDto;
        trainerRespDto = trainerMapper.toCredentialsDto(trainer);
        trainerRespDto = trainerTokenWrapperBuilder.wrap(trainerRespDto);

        return assembler.toModel(trainerRespDto);
    }

    // +. Get all Trainers
    @Operation(summary = "Get all trainers", security = @SecurityRequirement(name = "user_auth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TrainerProfile.class))
                    )
            )
    })
    @GetMapping("/trainers")
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<EntityModel<TrainerRespDto>> getAllTrainers(
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(30) Integer size
    )
    {
        Pageable pageable = PageRequest.of(page, size);
        return pagedAssembler.toModel(
                trainerService.getAllEntities(pageable)
                        .map(trainerMapper::toProfileDto)
        );
    }

    // 8. Get Trainer Profile
    @Operation(summary = "Get trainer profile", security = @SecurityRequirement(name = "user_auth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Trainer profile retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TrainerProfile.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content)
    })
    @GetMapping("/trainers/{username}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<TrainerRespDto> getTrainerByUsername(
            @Parameter(description = "Trainer's username", required = true)
            @PathVariable String username)
    {
        return assembler.toModel(
                Optional.ofNullable(trainerService.getUserByUsername(username))
                        .map(trainerMapper::toProfileDto)
                        .orElseThrow(ResourceNotFoundException::new)
        );
    }

    // 9. Update Trainer Profile
    @Operation(
            summary = "Update trainer profile", security = @SecurityRequirement(name = "user_auth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true, description = "Updated trainer data",
                    content = @Content(schema = @Schema(implementation = TrainerModificationRequest.class))
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Trainer profile updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TrainerProfile.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid trainer data provided", content = @Content)
    })
    @PutMapping("/trainers/{username}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<TrainerRespDto> updateTrainerByUsername(
            @Parameter(description = "Trainer's username", required = true)
            @PathVariable String username,

            @RequestBody @Valid TrainerModificationRequest trainerDto)
    {
        Trainer trainer = trainerMapper.toEntity(trainerDto);

        return assembler.toModel(
                Optional.ofNullable(trainerService.updateUserByUsername(username, trainer))
                        .map(trainerMapper::toProfileDto)
                        .orElseThrow(ResourceNotFoundException::new)
        );
    }

    // 16. Activate Trainer
    @Operation(summary = "Activate trainer profile", security = @SecurityRequirement(name = "user_auth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Trainer activated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TrainerRef.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content)
    })
    @PatchMapping("/trainers/{username}/activate")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<TrainerRespDto> activateTrainer(
            @Parameter(description = "Trainer's username", required = true)
            @PathVariable String username)
    {
        Optional.ofNullable(trainerService.activateUser(username))
                .orElseThrow(ResourceNotFoundException::new);

        TrainerRef trainerDto = new TrainerRef();
        trainerDto.setUsername(username);

        return assembler.toModel(trainerDto);
    }

    // 16. De-Activate Trainer
    @Operation(summary = "Deactivate trainer profile", security = @SecurityRequirement(name = "user_auth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Trainer deactivated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TrainerRef.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content)
    })
    @PatchMapping("/trainers/{username}/deactivate")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<TrainerRespDto> deactivateTrainer(
            @Parameter(description = "Trainer's username", required = true)
            @PathVariable String username
    )
    {
        Optional.ofNullable(trainerService.deactivateUser(username))
                .orElseThrow(ResourceNotFoundException::new);

        TrainerRef trainerDto = new TrainerRef();
        trainerDto.setUsername(username);

        return assembler.toModel(trainerDto);
    }

    // 3. Login
    @Operation(summary = "Log in as a trainer",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Trainer credentials", required = true,
                    content = @Content(schema = @Schema(implementation = TrainerLoginRequest.class))
            ))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Credentials verified successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TrainerTokenWrapper.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content),
            @ApiResponse(responseCode = "400", description = "Trainer data not properly structured", content = @Content)
    })
    @PostMapping("/trainers/login")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<TrainerRespDto> login(@RequestBody @Valid TrainerLoginRequest trainerDto)
    {
        return super.login(trainerDto);
    }

    // 4. Change Login
    @Operation(summary = "Change trainer password")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Password changed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TrainerTokenWrapper.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content),
            @ApiResponse(responseCode = "400", description = "Trainer data not properly structured", content = @Content)
    })
    @PutMapping("/trainers/change-password")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<TrainerRespDto> changePassword(@RequestBody @Valid TrainerChangePasswordRequest trainerDto)
    {
        return super.changePassword(trainerDto);
    }

    // 10. Get not assigned on trainee active trainers
    @Operation(summary = "Get active trainers not assigned to a specific trainee", security = @SecurityRequirement(name = "user_auth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "List of unassigned active trainers retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TrainerBriefProfile.class))
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content)
    })
    @GetMapping("/trainees/{traineeUsername}/trainers/unassigned")
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<EntityModel<TrainerRespDto>> getAllUnassignedForTraineeByUsername(
            @Parameter(description = "Trainee's username", required = true)
            @PathVariable String traineeUsername,

            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(30) Integer size
    )
    {
        Pageable pageable = PageRequest.of(page, size);

        Page<Trainer> trainers = Optional.ofNullable(
                trainerService.getAllUnassignedForTraineeByUsername(traineeUsername, pageable)
        ).orElseThrow(ResourceNotFoundException::new);

        return pagedAssembler.toModel(
                trainers.map(trainerMapper::toBriefProfileDto)
        );
    }

    // 11. Update Trainee's Trainer List
    @Operation(
            summary = "Update assigned trainers for a trainee", security = @SecurityRequirement(name = "user_auth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated set of trainers data", required = true,
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainerModificationEmbeddedRequest.class)))
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Assigned trainers updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TrainerBriefProfile.class))
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid trainer data provided", content = @Content)
    })
    @PutMapping("/trainees/{traineeUsername}/trainers/assigned")
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<EntityModel<TrainerRespDto>> updateAssignedTrainersForTrainee(
            @Parameter(description = "Trainee's username", required = true)
            @PathVariable String traineeUsername,
            @RequestBody Set<@Valid TrainerModificationEmbeddedRequest> trainerDtos)
    {
        Set<Trainer> trainers = trainerDtos.stream().map(trainerMapper::toEntity).collect(Collectors.toSet());

        Set<Trainer> updatedTrainers = Optional.ofNullable(
                trainerService.updateAssignedTrainersForTrainee(traineeUsername, trainers)
        ).orElseThrow(ResourceNotFoundException::new);

        return assembler.toCollectionModel(
                updatedTrainers.stream()
                        .map(trainerMapper::toBriefProfileDto)
                        .collect(Collectors.toSet())
        );
    }

    // +. Logout
    @Operation(summary = "Log out trainer", security = @SecurityRequirement(name = "user_auth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Logged out successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
    })
    @PostMapping("/trainers/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> logout(
            @Parameter(description = "Optional refresh token to revoke")
            @RequestHeader(name = "X-Refresh-Token", required = false) String refreshToken
    )
    {
        return super.logout(refreshToken);
    }

    // +. Refresh
    @Operation(summary = "Refresh trainer tokens")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Tokens rotated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TrainerTokenWrapper.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Missing required X-Refresh-Token header", content = @Content),
            @ApiResponse(responseCode = "403", description = "Token is invalid or lacks the appropriate role", content = @Content)
    })
    @PostMapping("/trainers/refresh")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<TrainerRespDto> refresh(
            @Parameter(description = "Refresh token consumed for rotation", required = true)
            @RequestHeader("X-Refresh-Token") String refreshToken
    )
    {
        return super.refresh(refreshToken, "TRAINER");
    }
}