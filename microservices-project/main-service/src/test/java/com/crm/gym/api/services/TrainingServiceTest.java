package com.crm.gym.api.services;

import com.crm.gym.api.entities.Trainee;
import com.crm.gym.api.entities.Trainer;
import com.crm.gym.api.entities.Training;
import com.crm.gym.api.entities.TrainingType;
import com.crm.gym.api.repositories.TrainingQueryCriteria;
import com.crm.gym.api.repositories.interfaces.TrainingRepository;
import com.crm.gym.client.reports.TrainerWorkloadClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TrainingService.class)
class TrainingServiceTest
{
    @Autowired private TrainingService trainingService;

    @MockitoBean private TrainingRepository trainingRepository;
    @MockitoBean private TrainerWorkloadClient trainerWorkloadClient;

    private static Training trainingMock;
    private static Training trainingSavedMock;
    private static List<Training> trainingsFoundMock;

    @BeforeAll
    static void beforeAll()
    {
        TrainingType trainingType = new TrainingType(UUID.randomUUID(), "Fitness");
        Trainee trainee = new Trainee(UUID.randomUUID(), "Alice", "Smith", "Alice.Smith", null, true, LocalDate.parse("1990-06-15"), "123 Main St");
        Trainer trainer = new Trainer(UUID.randomUUID(), "John", "Doe", "John.Doe", null, true, trainingType);

        Training unsavedTraining1 = new Training(null, "Full Body Fitness", LocalDate.parse("2025-06-07"), 60, trainee, trainer, trainingType);
        Training savedTraining1 = new Training(UUID.randomUUID(), unsavedTraining1.getName(), unsavedTraining1.getDate(), unsavedTraining1.getDuration(), unsavedTraining1.getTrainee(), unsavedTraining1.getTrainer(), unsavedTraining1.getTrainingType());
        Training savedTraining2 = new Training(UUID.randomUUID(), "HIIT Strength", LocalDate.parse("2025-06-15"), 55, trainee, trainer, trainingType);

        trainingMock = unsavedTraining1;
        trainingSavedMock = savedTraining1;
        trainingsFoundMock = List.of(savedTraining1, savedTraining2);
    }

    @Test
    @DisplayName("Should generate (id) and save Training")
    void saveEntity()
    {
        when(trainingRepository.create(trainingMock)).thenReturn(trainingSavedMock);

        Training trainingResult = trainingService.saveEntity(trainingMock);
        assertNotNull(trainingResult.getId());

        verify(trainingRepository).create(trainingMock);
    }

    @Test
    @DisplayName("Should retrieve an existent Training")
    void getEntityById()
    {
        UUID trainingSavedMockId = trainingSavedMock.getId();
        when(trainingRepository.findById(trainingSavedMockId)).thenReturn(Optional.of(trainingSavedMock));

        Training training = trainingService.getEntityById(trainingSavedMockId);
        assertNotNull(training);

        verify(trainingRepository).findById(trainingSavedMockId);
    }

    @Test
    @DisplayName("Should return null for non existent Training")
    void getEntityById2()
    {
        UUID nonExistentId = UUID.randomUUID();
        Training training = trainingService.getEntityById(nonExistentId);
        assertNull(training);

        verify(trainingRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should retrieve existing trainings by criteria")
    void getTrainingsByCriteria()
    {
        TrainingQueryCriteria criteria = TrainingQueryCriteria.builder()
                .traineeUsername("Alice.Smith")
                .trainerUsername("John.Doe")
                .fromDate(LocalDate.parse("2025-06-07"))
                .toDate(LocalDate.parse("2025-06-15"))
                .trainingTypeName("Fitness")
                .build();

        when(trainingRepository.findByCriteria(criteria)).thenReturn(trainingsFoundMock);

        List<Training> actualTrainings = trainingService.getTrainingsByCriteria(criteria);

        assertEquals(trainingsFoundMock.size(), actualTrainings.size());
        assertEquals(trainingsFoundMock, actualTrainings);

        verify(trainingRepository).findByCriteria(criteria);
    }
}