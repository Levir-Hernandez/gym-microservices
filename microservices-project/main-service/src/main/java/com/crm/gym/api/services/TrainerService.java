package com.crm.gym.api.services;

import com.crm.gym.api.entities.Trainer;
import com.crm.gym.api.factories.TrainerFactory;
import com.crm.gym.api.repositories.interfaces.TraineeRepository;
import com.crm.gym.api.repositories.interfaces.TrainerRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TrainerService extends UserService<Trainer, TrainerRepository>
{
    private TrainerFactory trainerFactory;
    private TraineeRepository traineeRepository;

    public TrainerService(TrainerRepository repository, TrainerFactory trainerFactory, TraineeRepository traineeRepository)
    {
        super(repository);
        this.trainerFactory = trainerFactory;
        this.traineeRepository = traineeRepository;
    }

    @Override
    public Trainer saveEntity(Trainer trainer)
    {
        trainer = trainerFactory.recreate(trainer);
        return super.saveEntity(trainer);
    }

    public List<Trainer> getAllUnassignedForTraineeByUsername(String username)
    {
        return Optional.of(username)
                .filter(traineeRepository::existsByUsername)
                .map(repository::findAllUnassignedForTraineeByUsername)
                .orElse(null);
    }

    public Page<Trainer> getAllUnassignedForTraineeByUsername(String username, Pageable pageable)
    {
        return Optional.of(username)
                .filter(traineeRepository::existsByUsername)
                .map(validTrainee -> repository.findAllUnassignedForTraineeByUsername(validTrainee, pageable))
                .orElse(null);
    }

    public Set<Trainer> updateAssignedTrainersForTrainee(String traineeUsername, Set<Trainer> trainers)
    {
        return Optional.of(traineeUsername)
                .filter(traineeRepository::existsByUsername)
                .map(validTrainee -> repository.updateAssignedTrainersForTrainee(validTrainee, trainers))
                .orElse(null);
    }
}
