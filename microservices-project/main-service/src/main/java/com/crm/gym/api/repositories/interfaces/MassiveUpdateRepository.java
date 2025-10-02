package com.crm.gym.api.repositories.interfaces;

import java.util.Set;
import com.crm.gym.api.entities.Trainer;

public interface MassiveUpdateRepository
{
        Set<Trainer> updateAssignedTrainersForTrainee(String traineeUsername, Set<Trainer> trainers);
}
