package com.crm.gym.api.services;

import com.crm.gym.api.entities.TrainerWorkloadSummary;
import com.crm.gym.api.repositories.TrainerWorkloadRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@SpringBootTest
@ActiveProfiles("test")
class TrainerWorkloadServiceTest
{
    @Autowired private TrainerWorkloadService trainerWorkloadService;

    @MockitoBean private TrainerWorkloadRepository trainerWorkloadRepository;

    @Test
    @DisplayName("Should increase workload for an existing Trainer")
    void shouldIncreaseWorkloadForExistingTrainer() {
        String username = "alice";
        LocalDate trainingDate = LocalDate.of(2025, 10, 1);
        TrainerWorkloadSummary existingSummary = new TrainerWorkloadSummary(username, "Alice", "Smith", true);
        existingSummary.getWorkloadSummary().putIfAbsent(2025, new HashMap<>());
        existingSummary.getWorkloadSummary().get(2025).put(10, 2);

        when(trainerWorkloadRepository.findByTrainerUsername(username)).thenReturn(existingSummary);
        when(trainerWorkloadRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TrainerWorkloadSummary result = trainerWorkloadService.increaseTrainerWorkload(
                username, "Alice", "Smith", true, trainingDate, 3
        );

        assertEquals(5, result.getWorkloadSummary().get(2025).get(10));
        verify(trainerWorkloadRepository).save(existingSummary);
    }

    @Test
    @DisplayName("Should create new Trainer workload if Trainer does not exist")
    void shouldCreateNewTrainerWorkload() {
        String username = "bob";
        LocalDate trainingDate = LocalDate.of(2025, 10, 1);

        when(trainerWorkloadRepository.findByTrainerUsername(username)).thenReturn(null);
        when(trainerWorkloadRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TrainerWorkloadSummary result = trainerWorkloadService.increaseTrainerWorkload(
                username, "Bob", "Jones", true, trainingDate, 4
        );

        assertEquals(4, result.getWorkloadSummary().get(2025).get(10));
        assertEquals("Bob", result.getTrainerFirstname());
        verify(trainerWorkloadRepository).save(result);
    }

    @Test
    @DisplayName("Should decrease workload for an existing Trainer")
    void shouldDecreaseWorkloadForExistingTrainer() {
        String username = "alice";
        LocalDate trainingDate = LocalDate.of(2025, 10, 1);
        TrainerWorkloadSummary summary = new TrainerWorkloadSummary(username, "Alice", "Smith", true);
        summary.getWorkloadSummary().put(2025, new HashMap<>());
        summary.getWorkloadSummary().get(2025).put(10, 5);

        when(trainerWorkloadRepository.findByTrainerUsername(username)).thenReturn(summary);
        when(trainerWorkloadRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TrainerWorkloadSummary result = trainerWorkloadService.decreaseTrainerWorkload(username, trainingDate, 3);

        assertEquals(2, result.getWorkloadSummary().get(2025).get(10));
        verify(trainerWorkloadRepository).save(summary);
    }

    @Test
    @DisplayName("Should remove month entry if workload decreases to zero")
    void shouldRemoveMonthEntryWhenWorkloadZero() {
        String username = "alice";
        LocalDate trainingDate = LocalDate.of(2025, 10, 1);
        TrainerWorkloadSummary summary = new TrainerWorkloadSummary(username, "Alice", "Smith", true);
        summary.getWorkloadSummary().put(2025, new HashMap<>());
        summary.getWorkloadSummary().get(2025).put(10, 3);

        when(trainerWorkloadRepository.findByTrainerUsername(username)).thenReturn(summary);
        when(trainerWorkloadRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TrainerWorkloadSummary result = trainerWorkloadService.decreaseTrainerWorkload(username, trainingDate, 3);

        assertNull(result.getWorkloadSummary().get(2025));
        verify(trainerWorkloadRepository).save(summary);
    }

    @Test
    @DisplayName("Should return null when trying to decrease workload for non-existent Trainer")
    void shouldReturnNullForNonExistentTrainerOnDecrease() {
        when(trainerWorkloadRepository.findByTrainerUsername("unknown")).thenReturn(null);

        TrainerWorkloadSummary result = trainerWorkloadService.decreaseTrainerWorkload("unknown", LocalDate.now(), 5);

        assertNull(result);
        verify(trainerWorkloadRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should retrieve Trainer workload by username")
    void shouldRetrieveTrainerWorkloadByUsername() {
        String username = "alice";
        TrainerWorkloadSummary summary = new TrainerWorkloadSummary(username, "Alice", "Smith", true);
        when(trainerWorkloadRepository.findByTrainerUsername(username)).thenReturn(summary);

        TrainerWorkloadSummary result = trainerWorkloadService.getTrainerWorkloadByUsername(username);

        assertEquals(summary, result);
    }

    @Test
    @DisplayName("Should retrieve all Trainers workload")
    void shouldRetrieveAllTrainersWorkload() {
        List<TrainerWorkloadSummary> list = List.of(
                new TrainerWorkloadSummary("alice", "Alice", "Smith", true),
                new TrainerWorkloadSummary("bob", "Bob", "Jones", true)
        );
        when(trainerWorkloadRepository.findAll()).thenReturn(list);

        List<TrainerWorkloadSummary> result = trainerWorkloadService.getAllTrainersWorkloads();

        assertEquals(2, result.size());
        assertEquals(list, result);
    }
}