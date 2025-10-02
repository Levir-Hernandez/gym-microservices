package com.crm.gym.api.dtos.trainingType;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.server.core.Relation;

@Getter @Setter
@Relation(collectionRelation = "trainingTypes")
public class TrainingTypeDto
{
    private String name;
}
