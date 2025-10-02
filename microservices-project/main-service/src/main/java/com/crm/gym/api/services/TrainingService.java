package com.crm.gym.api.services;

import com.crm.gym.api.entities.Training;
import com.crm.gym.api.repositories.TrainingQueryCriteria;
import com.crm.gym.api.repositories.interfaces.TrainingRepository;
import com.crm.gym.client.reports.TrainerWorkloadClient;
import com.crm.gym.client.reports.dtos.ActionType;
import com.crm.gym.client.reports.dtos.TrainerWorkloadRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TrainingService extends TemplateService<UUID, Training, TrainingRepository>
{
    private TrainerWorkloadClient trainerWorkloadClient;

    public TrainingService(TrainingRepository repository, TrainerWorkloadClient trainerWorkloadClient)
    {
        super(repository);
        this.trainerWorkloadClient = trainerWorkloadClient;
    }

    public List<Training> getTrainingsByCriteria(TrainingQueryCriteria trainingQueryCriteria)
    {
        return repository.findByCriteria(trainingQueryCriteria);
    }

    public Page<Training> getTrainingsByCriteria(TrainingQueryCriteria trainingQueryCriteria, Pageable pageable)
    {
        return repository.findByCriteria(trainingQueryCriteria, pageable);
    }

    @Override
    public Training saveEntity(Training training)
    {
        training = super.saveEntity(training);

        // Call reports-service to update trainer workload
        TrainerWorkloadRequest trainerWorkloadRequest = mapTrainingToTrainerWorkloadRequest(training, ActionType.ADD);
        trainerWorkloadClient.updateTrainerWorkload(trainerWorkloadRequest);

        return training;
    }

    public boolean deleteTrainingByName(String name)
    {
        Training training = repository.findByName(name).orElse(null);
        boolean deleted = repository.deleteByNameIfExists(name);

        if(deleted)
        {
            // Call reports-service to update trainer workload
            TrainerWorkloadRequest trainerWorkloadRequest = mapTrainingToTrainerWorkloadRequest(training, ActionType.DELETE);
            trainerWorkloadClient.updateTrainerWorkload(trainerWorkloadRequest);
        }

        return deleted;
    }

    // Helper to map Training to TrainerWorkloadRequest
    private TrainerWorkloadRequest mapTrainingToTrainerWorkloadRequest(Training training, ActionType actionType)
    {
        return Optional.ofNullable(training.getTrainer())
                .map(trainer -> new TrainerWorkloadRequest(
                        trainer.getUsername(),
                        trainer.getFirstname(),
                        trainer.getLastname(),
                        trainer.getIsActive(),
                        training.getDate(),
                        training.getDuration(),
                        actionType)
                ).orElse(null);
    }
}
