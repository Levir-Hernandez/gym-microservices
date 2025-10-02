package com.crm.gym.api.controllers;

import com.crm.gym.api.controllers.exceptions.ResourceNotFoundException;
import com.crm.gym.api.dtos.assemblers.TraineeModelAssembler;
import com.crm.gym.api.dtos.mappers.implementations.TraineeMapperImpl;
import com.crm.gym.api.dtos.trainee.*;
import com.crm.gym.api.entities.Trainee;
import com.crm.gym.api.services.TraineeService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.function.Supplier;

@RestController
@RequestMapping("/trainees")
@Tag(name = "Trainees", description = "Operations related to trainees")
public class TraineeController extends UserController<TraineeService, TraineeRespDto>
{
    private TraineeService traineeService;
    private TraineeMapperImpl traineeMapper;
    private TraineeModelAssembler assembler;
    private PagedResourcesAssembler<TraineeRespDto> pagedAssembler;
    private TraineeTokenWrapper.Builder traineeTokenWrapperBuilder;

    public TraineeController(TraineeService traineeService, TraineeMapperImpl traineeMapper, TraineeModelAssembler assembler, PagedResourcesAssembler<TraineeRespDto> pagedAssembler, TraineeTokenWrapper.Builder traineeTokenWrapperBuilder)
    {
        super(traineeService, assembler, traineeTokenWrapperBuilder);

        this.traineeService = traineeService;
        this.traineeMapper = traineeMapper;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
        this.traineeTokenWrapperBuilder = traineeTokenWrapperBuilder;
    }

    @Override
    protected Supplier<TraineeRespDto> userRefSupplier() {return TraineeRef::new;}

    // 1. Trainee Registration
    @Operation(
            summary = "Register a new trainee",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "Trainee data", required = true,
                            content = @Content(schema = @Schema(implementation = TraineeRegistrationRequest.class))
                    )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201", description = "Trainee registered successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TraineeCredentials.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid trainee data provided", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<TraineeRespDto> createTrainee(@RequestBody @Valid TraineeRegistrationRequest traineeDto)
    {
        Trainee trainee = traineeMapper.toEntity(traineeDto);
        trainee = traineeService.saveEntity(trainee);

        TraineeRespDto traineeRespDto;
        traineeRespDto = traineeMapper.toCredentialsDto(trainee);
        traineeRespDto = traineeTokenWrapperBuilder.wrap(traineeRespDto);

        return assembler.toModel(traineeRespDto);
    }

    // +. Get all Trainees
    @Operation(summary = "Get all trainees", security = @SecurityRequirement(name = "user_auth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TraineeProfile.class))
                    )
            )
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<EntityModel<TraineeRespDto>> getAllTrainees(
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(30) Integer size
    )
    {
        Pageable pageable = PageRequest.of(page, size);
        return pagedAssembler.toModel(
                traineeService.getAllEntities(pageable)
                        .map(traineeMapper::toProfileDto)
        );
    }

    // 5. Get Trainee Profile
    @Operation(summary = "Get trainee profile", security = @SecurityRequirement(name = "user_auth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Trainee profile retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TraineeProfile.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content)
    })
    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<TraineeRespDto> getTraineeByUsername(
            @Parameter(description = "Trainee's username", required = true)
            @PathVariable String username)
    {
        return Optional.ofNullable(traineeService.getUserByUsername(username))
                .map(traineeMapper::toProfileDto)
                .map(assembler::toModel)
                .orElseThrow(ResourceNotFoundException::new);
    }

    // 6. Update Trainee Profile
    @Operation(
            summary = "Update trainee profile", security = @SecurityRequirement(name = "user_auth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true, description = "Updated trainee data",
                    content = @Content(schema = @Schema(implementation = TraineeModificationRequest.class))
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Trainee profile updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TraineeProfile.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid trainee data provided", content = @Content)
    })
    @PutMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<TraineeRespDto> updateTraineeByUsername(
            @Parameter(description = "Trainee's username", required = true)
            @PathVariable String username,

            @RequestBody @Valid TraineeModificationRequest traineeDto)
    {
        Trainee trainee = traineeMapper.toEntity(traineeDto);

        return Optional.ofNullable(traineeService.updateUserByUsername(username, trainee))
                .map(traineeMapper::toProfileDto)
                .map(assembler::toModel)
                .orElseThrow(ResourceNotFoundException::new);
    }

    // 15. Activate Trainee
    @Operation(summary = "Activate trainee profile", security = @SecurityRequirement(name = "user_auth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Trainee activated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TraineeRef.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content)
    })
    @PatchMapping("/{username}/activate")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<TraineeRespDto> activateTrainee(
            @Parameter(description = "Trainee's username", required = true)
            @PathVariable String username
    )
    {
        Optional.ofNullable(traineeService.activateUser(username))
                .orElseThrow(ResourceNotFoundException::new);

        TraineeRef traineeDto = new TraineeRef();
        traineeDto.setUsername(username);

        return assembler.toModel(traineeDto);
    }

    // 15. De-Activate Trainee
    @Operation(summary = "Deactivate trainee profile", security = @SecurityRequirement(name = "user_auth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Trainee deactivated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TraineeRef.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content)
    })
    @PatchMapping("/{username}/deactivate")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<TraineeRespDto> deactivateTrainee(
            @Parameter(description = "Trainee's username", required = true)
            @PathVariable String username
    )
    {
        Optional.ofNullable(traineeService.deactivateUser(username))
                .orElseThrow(ResourceNotFoundException::new);

        TraineeRef traineeDto = new TraineeRef();
        traineeDto.setUsername(username);

        return assembler.toModel(traineeDto);
    }

    // 7. Delete Trainee Profile
    @Operation(summary = "Delete trainee profile", security = @SecurityRequirement(name = "user_auth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Trainee deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content)
    })
    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteTraineeByUsername(
            @Parameter(description = "Trainee's username", required = true)
            @PathVariable String username
    )
    {
        boolean deleted = traineeService.deleteTraineeByUsername(username);
        if(!deleted){throw new ResourceNotFoundException();}

        return ResponseEntity.noContent()
                .header("X-Resource-Id", username)
                .build();
    }

    // 3. Login
    @Operation(summary = "Log in as a trainee",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Trainee credentials", required = true,
                    content = @Content(schema = @Schema(implementation = TraineeLoginRequest.class))
            ))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Credentials verified successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TraineeTokenWrapper.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content),
            @ApiResponse(responseCode = "400", description = "Trainee data not properly structured", content = @Content)
    })
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<TraineeRespDto> login(@RequestBody @Valid TraineeLoginRequest traineeDto)
    {
        return super.login(traineeDto);
    }

    // 4. Change Login
    @Operation(summary = "Change trainee password")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Password changed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TraineeTokenWrapper.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content),
            @ApiResponse(responseCode = "400", description = "Trainee data not properly structured", content = @Content)
    })
    @PutMapping("/change-password")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<TraineeRespDto> changePassword(@RequestBody @Valid TraineeChangePasswordRequest traineeDto)
    {
        return super.changePassword(traineeDto);
    }

    // +. Logout
    @Operation(summary = "Log out trainee", security = @SecurityRequirement(name = "user_auth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Logged out successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
    })
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> logout(
            @Parameter(description = "Optional refresh token to revoke")
            @RequestHeader(name = "X-Refresh-Token", required = false) String refreshToken
    )
    {
        return super.logout(refreshToken);
    }

    // +. Refresh
    @Operation(summary = "Refresh trainee tokens")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Tokens rotated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TraineeTokenWrapper.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Missing required X-Refresh-Token header", content = @Content),
            @ApiResponse(responseCode = "403", description = "Token is invalid or lacks the appropriate role", content = @Content)
    })
    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<TraineeRespDto> refresh(
            @Parameter(description = "Refresh token consumed for rotation", required = true)
            @RequestHeader("X-Refresh-Token") String refreshToken
    )
    {
        return super.refresh(refreshToken, "TRAINEE");
    }
}
