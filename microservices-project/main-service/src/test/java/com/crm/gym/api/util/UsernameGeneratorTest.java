package com.crm.gym.api.util;

import com.crm.gym.api.entities.Trainee;
import com.crm.gym.api.entities.Trainer;
import com.crm.gym.api.services.TraineeService;
import com.crm.gym.api.services.TrainerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UsernameGeneratorTest
{
    private TraineeService traineeService;
    private TrainerService trainerService;

    @Autowired
    public UsernameGeneratorTest(TraineeService traineeService, TrainerService trainerService)
    {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    @Test
    @DisplayName("Should generate a username following 'firstname.lastname' format")
    void generateUsername()
    {
        Trainee trainee = new Trainee(null,
                "Larry", "Williams",
                null, null,
                true, LocalDate.parse("1991-03-21"), "123 Harlem St");
        String expectedUsername = trainee.getFirstname() + "." + trainee.getLastname();

        Trainee traineeResult = traineeService.saveEntity(trainee);
        String actualUsername = traineeResult.getUsername();

        assertEquals(expectedUsername, actualUsername);
    }

    @Test
    @DisplayName("Should append a serial number to usernames in case of collision")
    void generateUsername2()
    {
        Trainee trainee = new Trainee(null,
                "Clarice", "Starling",
                null, null,
                true, LocalDate.parse("1991-03-21"), "123 Harlem St");
        String expectedUsername = trainee.getFirstname() + "." + trainee.getLastname();

        Trainee traineeResult = traineeService.saveEntity(trainee);
        String actualUsername = traineeResult.getUsername();

        assertEquals(expectedUsername, actualUsername);

        traineeResult = traineeService.saveEntity(trainee);
        actualUsername = traineeResult.getUsername();

        assertEquals(expectedUsername+"1", actualUsername);

        traineeResult = traineeService.saveEntity(trainee);
        actualUsername = traineeResult.getUsername();

        assertEquals(expectedUsername+"2", actualUsername);
    }

    @Test
    @DisplayName("Should apply the same collision resolution for all user types (Trainees and Trainers)")
    void generateUsername3()
    {
        Trainee trainee = new Trainee(null,
                "Clarice", "Starling",
                null, null,
                true, LocalDate.parse("1991-03-21"), "123 Harlem St");
        String expectedUsername = trainee.getFirstname() + "." + trainee.getLastname();

        Trainee traineeResult = traineeService.saveEntity(trainee);
        String actualUsername = traineeResult.getUsername();

        assertEquals(expectedUsername, actualUsername);

        traineeResult = traineeService.saveEntity(trainee);
        actualUsername = traineeResult.getUsername();

        assertEquals(expectedUsername+"1", actualUsername);

        Trainer trainer = new Trainer(null,
                "Clarice", "Starling",
                null, null,
                true, null);

        Trainer trainerResult = trainerService.saveEntity(trainer);
        actualUsername = trainerResult.getUsername();

        assertEquals(expectedUsername+"2", actualUsername);
    }
}