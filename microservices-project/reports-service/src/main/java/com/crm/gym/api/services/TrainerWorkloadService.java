package com.crm.gym.api.services;

import com.crm.gym.api.entities.TrainerWorkloadSummary;
import com.crm.gym.api.repositories.TrainerWorkloadRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TrainerWorkloadService
{
    private TrainerWorkloadRepository trainerWorkloadRepository;

    public TrainerWorkloadService(TrainerWorkloadRepository trainerWorkloadRepository)
    {
        this.trainerWorkloadRepository = trainerWorkloadRepository;
    }

    public TrainerWorkloadSummary increaseTrainerWorkload(
            String trainerUsername, String trainerFirstname, String trainerLastname, Boolean trainerStatus,
            LocalDate trainingDate, Integer trainingDuration
    )
    {
        TrainerWorkloadSummary trainerWorkloadSummary = Optional.ofNullable(trainerWorkloadRepository.findByTrainerUsername(trainerUsername))
                .orElse(new TrainerWorkloadSummary(trainerUsername, trainerFirstname, trainerLastname, trainerStatus));

        int year = trainingDate.getYear();
        int month = trainingDate.getMonthValue();

        trainerWorkloadSummary.getWorkloadSummary()
                .computeIfAbsent(year, HashMap::new)
                .merge(month, trainingDuration, Integer::sum);

        return trainerWorkloadRepository.save(trainerWorkloadSummary);
    }

    public TrainerWorkloadSummary decreaseTrainerWorkload(String trainerUsername, LocalDate trainingDate, Integer trainingDuration)
    {
        TrainerWorkloadSummary trainerWorkloadSummary = trainerWorkloadRepository.findByTrainerUsername(trainerUsername);
        if(trainerWorkloadSummary == null){return null;}

        int year = trainingDate.getYear();
        int month = trainingDate.getMonthValue();

        Map<Integer, Map<Integer, Integer>> annualWorkloadSummary = trainerWorkloadSummary.getWorkloadSummary();
        Map<Integer, Integer> monthlyWorkloadSummary = annualWorkloadSummary.get(year);

        if(monthlyWorkloadSummary == null){return trainerWorkloadSummary;}

        monthlyWorkloadSummary.merge(month, trainingDuration, (a,b) -> Math.max(0, a-b));

        monthlyWorkloadSummary.values().removeIf(totalTrainingDuration -> totalTrainingDuration == 0);
        annualWorkloadSummary.values().removeIf(Map::isEmpty);

        return trainerWorkloadRepository.save(trainerWorkloadSummary);
    }

    public TrainerWorkloadSummary getTrainerWorkloadByUsername(String trainerUsername)
    {
        return trainerWorkloadRepository.findByTrainerUsername(trainerUsername);
    }

    public List<TrainerWorkloadSummary> getAllTrainersWorkloads()
    {
        return trainerWorkloadRepository.findAll();
    }
}
