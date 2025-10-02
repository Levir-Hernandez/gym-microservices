package com.crm.gym.api.dtos.assemblers;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.EntityModel;
import com.crm.gym.api.dtos.training.TrainingRespDto;
import com.crm.gym.api.controllers.TrainingController;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TrainingModelAssembler implements RepresentationModelAssembler<TrainingRespDto, EntityModel<TrainingRespDto>>
{
    @Override
    public EntityModel<TrainingRespDto> toModel(TrainingRespDto trainingRespDto)
    {
        return EntityModel.of(trainingRespDto, buildLinks(trainingRespDto.getName()));
    }

    public Link[] buildLinks(String name)
    {
        return new Link[]{
                linkTo(methodOn(TrainingController.class).createTraining(null))
                        .withRel("create-training"),
                linkTo(methodOn(TrainingController.class).deleteTrainingByName(name))
                        .withRel("delete-training"),
                linkTo(methodOn(TrainingController.class).getAllTrainings(null, null))
                        .withRel("all-trainings"),
                linkTo(methodOn(TrainingController.class).getTrainingsByTraineeUsernameAndCriteria(null, null, null, null, null, null, null))
                        .withRel("trainee-trainings"),
                linkTo(methodOn(TrainingController.class).getTrainingsByTrainerUsernameAndCriteria(null, null, null, null, null, null))
                        .withRel("trainer-trainings")
        };
    }
}
