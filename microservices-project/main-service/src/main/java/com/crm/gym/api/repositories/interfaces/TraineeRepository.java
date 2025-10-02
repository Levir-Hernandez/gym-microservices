package com.crm.gym.api.repositories.interfaces;

import java.util.Optional;
import com.crm.gym.api.entities.Trainee;
import jakarta.transaction.Transactional;

public interface TraineeRepository extends UserRepository<Trainee>
{
    void deleteByUsername(String username);

    @Transactional
    default boolean deleteByUsernameIfExists(String username)
    {
        Optional<Trainee> deletableTrainee = findByUsername(username);
        deletableTrainee.ifPresent(t -> deleteByUsername(username));
        return deletableTrainee.isPresent();
    }
}
