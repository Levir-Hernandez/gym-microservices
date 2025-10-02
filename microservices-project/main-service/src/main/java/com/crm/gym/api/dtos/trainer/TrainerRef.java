package com.crm.gym.api.dtos.trainer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.server.core.Relation;

@Getter @Setter
@Relation(collectionRelation = "trainees")
public class TrainerRef implements TrainerRespDto
{
    private String username;
}