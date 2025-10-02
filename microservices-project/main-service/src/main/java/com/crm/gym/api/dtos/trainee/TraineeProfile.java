package com.crm.gym.api.dtos.trainee;

import lombok.Getter;
import lombok.Setter;
import com.crm.gym.api.dtos.trainer.TrainerBriefProfile;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;
import java.time.LocalDate;

@Getter @Setter
@Relation(collectionRelation = "trainees")
public class TraineeProfile extends TraineeBriefProfile
{
    private LocalDate birthdate;
    private String address;
    private Boolean isActive;

    private List<TrainerBriefProfile> trainers;
}
