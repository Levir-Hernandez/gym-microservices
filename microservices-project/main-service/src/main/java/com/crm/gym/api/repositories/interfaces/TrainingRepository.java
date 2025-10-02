package com.crm.gym.api.repositories.interfaces;

import com.crm.gym.api.entities.Training;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface TrainingRepository extends DynamicQueryRepository, TemplateRepository<UUID, Training>
{
    Optional<Training> findByName(String name);
    void deleteByName(String name);

    @Transactional
    default boolean deleteByNameIfExists(String name)
    {
        Optional<Training> deletableTraining = findByName(name);
        deletableTraining.ifPresent(t -> deleteByName(name));
        return deletableTraining.isPresent();
    }
}
