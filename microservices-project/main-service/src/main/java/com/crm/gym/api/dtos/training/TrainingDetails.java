package com.crm.gym.api.dtos.training;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;

@Getter @Setter
@Relation(collectionRelation = "trainings")
public class TrainingDetails implements TrainingRespDto
{
    private String name;
    private String trainingType;
    private LocalDate date;
    private Integer duration;
    private String trainerUsername;
    private String traineeUsername;
}
