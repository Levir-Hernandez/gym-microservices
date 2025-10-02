package com.crm.gym.api.dtos.trainee;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.server.core.Relation;

@Getter @Setter
@Relation(collectionRelation = "trainees")
public class TraineeBriefProfile extends TraineeRef
{
    private String firstname;
    private String lastname;
}
