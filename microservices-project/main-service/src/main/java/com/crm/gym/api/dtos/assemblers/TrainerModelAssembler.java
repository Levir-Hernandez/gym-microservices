package com.crm.gym.api.dtos.assemblers;

import org.springframework.hateoas.Link;
import com.crm.gym.api.dtos.trainer.TrainerRespDto;
import org.springframework.hateoas.EntityModel;
import com.crm.gym.api.controllers.TrainerController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TrainerModelAssembler implements RepresentationModelAssembler<TrainerRespDto, EntityModel<TrainerRespDto>>
{
    @Override
    public EntityModel<TrainerRespDto> toModel(TrainerRespDto trainerRespDto)
    {
        return EntityModel.of(trainerRespDto, buildLinks(trainerRespDto.getUsername()));
    }

    @Override
    public CollectionModel<EntityModel<TrainerRespDto>> toCollectionModel(Iterable<? extends TrainerRespDto> trainerRespDtos)
    {
        Collection<EntityModel<TrainerRespDto>> entityModels = StreamSupport.stream(
                        trainerRespDtos.spliterator(), false
                )
                .map(EntityModel::<TrainerRespDto>of)
                .collect(Collectors.toList());

        return CollectionModel.of(entityModels, buildLinks(null));
    }

    public Link[] buildLinks(String username)
    {
        return new Link[]{
                linkTo(methodOn(TrainerController.class).createTrainer(null))
                        .withRel("create-trainer"),
                linkTo(methodOn(TrainerController.class).getAllTrainers(null, null))
                        .withRel("all-trainers"),
                linkTo(methodOn(TrainerController.class).getTrainerByUsername(username))
                        .withRel("trainer-by-username"),
                linkTo(methodOn(TrainerController.class).updateTrainerByUsername(username, null))
                        .withRel("update-trainer"),
                linkTo(methodOn(TrainerController.class).activateTrainer(username))
                        .withRel("activate-trainer"),
                linkTo(methodOn(TrainerController.class).deactivateTrainer(username))
                        .withRel("deactivate-trainer"),
                linkTo(methodOn(TrainerController.class).login(null))
                        .withRel("authenticate-trainer"),
                linkTo(methodOn(TrainerController.class).changePassword(null))
                        .withRel("update-trainer-password"),
                linkTo(methodOn(TrainerController.class).logout(null))
                        .withRel("logout-trainer"),
                linkTo(methodOn(TrainerController.class).refresh(null))
                        .withRel("refresh-trainer-tokens"),
                linkTo(methodOn(TrainerController.class).getAllUnassignedForTraineeByUsername(username, null, null))
                        .withRel("unassigned-trainers-for-trainee"),
                linkTo(methodOn(TrainerController.class).updateAssignedTrainersForTrainee(username, null))
                        .withRel("bulk-update-trainers-for-trainee")
        };
    }
}
