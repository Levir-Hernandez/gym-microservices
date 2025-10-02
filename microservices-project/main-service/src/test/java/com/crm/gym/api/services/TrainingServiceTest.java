package com.crm.gym.api.services;

import com.crm.gym.api.entities.Training;
import com.crm.gym.api.repositories.TrainingQueryCriteria;
import com.crm.gym.api.services.TrainingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TrainingServiceTest
{
    private TrainingService trainingService;

    @Autowired
    public TrainingServiceTest(TrainingService trainingService)
    {
        this.trainingService = trainingService;
    }

    @Test
    @DisplayName("Should generate (id) and save Training")
    void saveEntity()
    {
        int totalTrainings = trainingService.getAllEntities().size();

        Training training = new Training(null,
                "Some Training", LocalDate.parse("2021-06-07"), 30,
                null, null, null);

        Training trainingResult = trainingService.saveEntity(training);

        assertNotNull(trainingResult.getId());

        int actualTrainings = trainingService.getAllEntities().size();
        assertEquals(totalTrainings+1, actualTrainings);
    }

    @Test
    @DisplayName("Should retrieve an existent Training")
    void getEntityById()
    {
        Training training = new Training(null,
                "Some Training", LocalDate.parse("2021-06-07"), 30,
                null, null, null);

        Training trainingExpected = trainingService.saveEntity(training);

        UUID id = trainingExpected.getId();
        Training trainingActual = trainingService.getEntityById(id);

        assertEquals(trainingExpected, trainingActual);
    }

    @Test
    @DisplayName("Should return null for non existent Training")
    void getEntityById2()
    {
        UUID nonExistentId = UUID.randomUUID();
        Training training = trainingService.getEntityById(nonExistentId);
        assertNull(training);
    }

    @Test
    @DisplayName("Should retrieve existing trainings by criteria")
    void getTrainingsByCriteria()
    {
        List<Training> expectedTrainings = trainingService.getAllEntities().subList(0,2);

        TrainingQueryCriteria criteria = TrainingQueryCriteria.builder()
                .traineeUsername("Alice.Smith")
                .trainerUsername("John.Doe")
                .fromDate(LocalDate.parse("2025-06-07"))
                .toDate(LocalDate.parse("2025-06-15"))
                .trainingTypeName("Fitness")
                .build();

        List<Training> actualTrainings = trainingService.getTrainingsByCriteria(criteria);

        assertEquals(expectedTrainings.size(), actualTrainings.size());
        assertEquals(expectedTrainings, actualTrainings);
    }
}