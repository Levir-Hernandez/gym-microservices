package com.crm.gym.api.dtos.assemblers;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.EntityModel;
import com.crm.gym.api.dtos.trainingType.TrainingTypeDto;
import com.crm.gym.api.controllers.TrainingTypeController;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

// Assembler currently unused due no resource-specific actions
@Component
public class TrainingTypeModelAssembler implements RepresentationModelAssembler<TrainingTypeDto, EntityModel<TrainingTypeDto>>
{
    @Override
    public EntityModel<TrainingTypeDto> toModel(TrainingTypeDto trainingTypeDto)
    {
        return EntityModel.of(trainingTypeDto, buildLinks());
    }

    public Link[] buildLinks()
    {
        return new Link[]{
                linkTo(methodOn(TrainingTypeController.class).getAllTrainingTypes(null, null))
                        .withRel("all-trainings")
        };
    }
}
