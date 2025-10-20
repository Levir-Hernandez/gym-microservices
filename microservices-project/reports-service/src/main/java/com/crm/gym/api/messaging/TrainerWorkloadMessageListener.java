package com.crm.gym.api.messaging;

import java.util.List;
import com.crm.gym.api.dtos.ActionType;
import com.crm.gym.api.dtos.TrainerWorkloadRequest;
import com.crm.gym.api.entities.TrainerWorkloadSummary;
import com.crm.gym.api.services.TrainerWorkloadService;

import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class TrainerWorkloadMessageListener
{
    private TrainerWorkloadService trainerWorkloadService;

    public TrainerWorkloadMessageListener(TrainerWorkloadService trainerWorkloadService)
    {
        this.trainerWorkloadService = trainerWorkloadService;
    }

    @JmsListener(destination = "#{@trainerWorkloadQueueProperties.getGetOneRequest()}")
    @SendTo("#{@trainerWorkloadQueueProperties.getGetOneResponse()}")
    public TrainerWorkloadSummary getTrainerWorkloadByUsername(String trainerUsername)
    {
        return trainerWorkloadService.getTrainerWorkloadByUsername(trainerUsername);
    }

    @JmsListener(destination = "#{@trainerWorkloadQueueProperties.getGetAllRequest()}")
    @SendTo("#{@trainerWorkloadQueueProperties.getGetAllResponse()}")
    public List<TrainerWorkloadSummary> getAllTrainersWorkloads()
    {
        return trainerWorkloadService.getAllTrainersWorkloads();
    }

    @JmsListener(destination = "#{@trainerWorkloadQueueProperties.getUpdateRequest()}")
    public void updateTrainerWorkload(TrainerWorkloadRequest trainerWorkloadRequest)
    {
        switch (trainerWorkloadRequest.getActionType())
        {
            case ActionType.ADD -> trainerWorkloadService.increaseTrainerWorkload(
                    trainerWorkloadRequest.getTrainerUsername(),
                    trainerWorkloadRequest.getTrainerFirstname(),
                    trainerWorkloadRequest.getTrainerLastname(),
                    trainerWorkloadRequest.getTrainerStatus(),
                    trainerWorkloadRequest.getTrainingDate(),
                    trainerWorkloadRequest.getTrainingDuration()
            );
            case ActionType.DELETE -> trainerWorkloadService.decreaseTrainerWorkload(
                    trainerWorkloadRequest.getTrainerUsername(),
                    trainerWorkloadRequest.getTrainingDate(),
                    trainerWorkloadRequest.getTrainingDuration()
            );
        }
    }
}
