package com.crm.gym.api.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.crm.gym.api.entities.TrainerWorkloadSummary;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerWorkloadRepository extends MongoRepository<TrainerWorkloadSummary, String>
{
    TrainerWorkloadSummary findByTrainerUsername(String trainerUsername);
    TrainerWorkloadSummary findByTrainerFirstnameAndTrainerLastname(String trainerFirstname, String trainerLastname);
}
