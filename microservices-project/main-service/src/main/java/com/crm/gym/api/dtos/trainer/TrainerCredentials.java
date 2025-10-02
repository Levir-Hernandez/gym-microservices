package com.crm.gym.api.dtos.trainer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.server.core.Relation;

@Getter @Setter
@Relation(collectionRelation = "trainers")
public class TrainerCredentials extends TrainerRef
{
    private String password;
}