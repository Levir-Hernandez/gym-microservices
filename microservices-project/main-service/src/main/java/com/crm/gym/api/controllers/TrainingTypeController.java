package com.crm.gym.api.controllers;

import com.crm.gym.api.dtos.mappers.interfaces.TrainingTypeMapper;
import com.crm.gym.api.dtos.trainingType.TrainingTypeDto;
import com.crm.gym.api.repositories.interfaces.TrainingTypeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/trainingTypes")
@Tag(name = "Training Types", description = "Operations related to training types")
public class TrainingTypeController
{
    private TrainingTypeRepository trainingTypeRepository;
    private TrainingTypeMapper trainingTypeMapper;
    private PagedResourcesAssembler<TrainingTypeDto> pagedAssembler;

    public TrainingTypeController(TrainingTypeRepository trainingTypeRepository, TrainingTypeMapper trainingTypeMapper, PagedResourcesAssembler<TrainingTypeDto> pagedAssembler)
    {
        this.trainingTypeRepository = trainingTypeRepository;
        this.trainingTypeMapper = trainingTypeMapper;
        this.pagedAssembler = pagedAssembler;
    }

    // 17. Get Training types
    @Operation(summary = "Get all training types", security = @SecurityRequirement(name = "user_auth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TrainingTypeDto.class))
                    )
            )
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<EntityModel<TrainingTypeDto>> getAllTrainingTypes(
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer page,
            @RequestParam(required = false, defaultValue = "5") @Min(1) @Max(5) Integer size
    )
    {
        Pageable pageable = PageRequest.of(page, size);
        return pagedAssembler.toModel(
                trainingTypeRepository.findAll(pageable).map(trainingTypeMapper::toDto)
        );
    }
}