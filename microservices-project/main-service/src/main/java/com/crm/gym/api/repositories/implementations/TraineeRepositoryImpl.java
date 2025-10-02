package com.crm.gym.api.repositories.implementations;

import com.crm.gym.api.entities.Trainee;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class TraineeRepositoryImpl extends TemplateRepositoryImpl<UUID, Trainee>
{
    @Override
    protected Class<Trainee> getEntityClass() {return Trainee.class;}
}
