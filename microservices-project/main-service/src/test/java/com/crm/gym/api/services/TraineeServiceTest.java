package com.crm.gym.api.services;

import com.crm.gym.api.entities.Trainee;
import com.crm.gym.api.services.TraineeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TraineeServiceTest
{
    private TraineeService traineeService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public TraineeServiceTest(TraineeService traineeService, PasswordEncoder passwordEncoder)
    {
        this.traineeService = traineeService;
        this.passwordEncoder = passwordEncoder;
    }

    @Test
    @DisplayName("Should generate (id,username,password) and save Trainee")
    void saveEntity()
    {
        int totalTrainees = traineeService.getAllEntities().size();

        Trainee trainee = new Trainee(null,
                "Larry", "Williams",
                null, null,
                true, LocalDate.parse("1991-03-21"), "123 Harlem St");

        Trainee traineeResult = traineeService.saveEntity(trainee);

        assertNotNull(traineeResult.getId());
        assertNotNull(traineeResult.getUsername());
        assertNotNull(traineeResult.getPassword());

        int actualTrainees = traineeService.getAllEntities().size();
        assertEquals(totalTrainees+1, actualTrainees);
    }

    @Test
    @DisplayName("Should update existing Trainee")
    void updateEntity()
    {
        UUID id = traineeService.getUserByUsername("Larry.Williams").getId();

        Trainee newTraineeInfo = new Trainee(null,
                "Larry", "Williams",
                "Willy", "secret1234",
                false, LocalDate.parse("1991-03-21"), "123 Harlem St");

        Trainee oldTrainee = traineeService.getEntityById(id);

        assertNotEquals(newTraineeInfo, oldTrainee);

        traineeService.updateEntity(id, newTraineeInfo);
        Trainee newTrainee = traineeService.getEntityById(id);

        assertNotEquals(oldTrainee, newTrainee);
        assertEquals(newTraineeInfo, newTrainee);
    }

    @Test
    @DisplayName("Should not update non existing Trainee")
    void updateEntity2()
    {
        int totalTrainees = traineeService.getAllEntities().size();
        UUID nonExistentId = UUID.randomUUID();

        Trainee newTraineeInfo = new Trainee(null,
                "Larry", "Williams",
                "Willy", "secret1234",
                false, LocalDate.parse("1991-03-21"), "123 Harlem St");

        Trainee oldTrainee = traineeService.getEntityById(nonExistentId);
        assertNull(oldTrainee);

        traineeService.updateEntity(nonExistentId, newTraineeInfo);

        Trainee newTrainee = traineeService.getEntityById(nonExistentId);
        assertNull(newTrainee);

        int actualTrainees = traineeService.getAllEntities().size();
        assertEquals(totalTrainees, actualTrainees);
    }

    @Test
    @DisplayName("Should update existing Trainee by username")
    void updateUserByUsername()
    {
        String username = "Larry.Williams";
        Trainee newTraineeInfo = new Trainee(null,
                "Larry", "Williams",
                "Willy", "secret1234",
                false, LocalDate.parse("1991-03-21"), "123 Harlem St");

        Trainee oldTrainee = traineeService.getUserByUsername(username);

        assertNotEquals(newTraineeInfo, oldTrainee);

        traineeService.updateUserByUsername(username, newTraineeInfo);
        Trainee newTrainee = traineeService.getUserByUsername(username);

        assertNotEquals(oldTrainee, newTrainee);
        assertEquals(newTraineeInfo, newTrainee);
    }

    @Test
    @DisplayName("Should not update non existing Trainee by username")
    void updateUserByUsername2()
    {
        int totalTrainees = traineeService.getAllEntities().size();
        String username = "Unknown.Unknown";

        Trainee newTraineeInfo = new Trainee(null,
                "Larry", "Williams",
                "Willy", "secret1234",
                false, LocalDate.parse("1991-03-21"), "123 Harlem St");

        Trainee oldTrainee = traineeService.getUserByUsername(username);
        assertNull(oldTrainee);

        traineeService.updateUserByUsername(username, newTraineeInfo);

        Trainee newTrainee = traineeService.getUserByUsername(username);
        assertNull(newTrainee);

        int actualTrainees = traineeService.getAllEntities().size();
        assertEquals(totalTrainees, actualTrainees);
    }

    @Test
    @DisplayName("Should delete existing Trainee")
    void deleteEntity()
    {
        UUID id = traineeService.getUserByUsername("Ethan.Davis").getId();

        Trainee trainee = traineeService.getEntityById(id);
        assertNotNull(trainee);

        traineeService.deleteEntity(id);

        trainee = traineeService.getEntityById(id);
        assertNull(trainee);
    }

    @Test
    @DisplayName("Should not delete non existing Trainee")
    void deleteEntity2()
    {
        int totalTrainees = traineeService.getAllEntities().size();
        UUID nonExistentId = UUID.randomUUID();

        Trainee trainee = traineeService.getEntityById(nonExistentId);
        assertNull(trainee);

        traineeService.deleteEntity(nonExistentId);

        trainee = traineeService.getEntityById(nonExistentId);
        assertNull(trainee);

        int actualTrainees = traineeService.getAllEntities().size();
        assertEquals(totalTrainees, actualTrainees);
    }

    @Test
    @DisplayName("Should delete existing Trainee by username")
    void deleteTraineeByUsername()
    {
        String username = "Bob.Johnson";

        Trainee trainee = traineeService.getUserByUsername(username);
        assertNotNull(trainee);

        traineeService.deleteTraineeByUsername(username);

        trainee = traineeService.getUserByUsername(username);
        assertNull(trainee);
    }

    @Test
    @DisplayName("Should not delete non existing Trainee by username")
    void deleteTraineeByUsername2()
    {
        int totalTrainees = traineeService.getAllEntities().size();
        String username = "Unknown.Unknown";

        Trainee trainee = traineeService.getUserByUsername(username);
        assertNull(trainee);

        traineeService.deleteTraineeByUsername(username);

        trainee = traineeService.getUserByUsername(username);
        assertNull(trainee);

        int actualTrainees = traineeService.getAllEntities().size();
        assertEquals(totalTrainees, actualTrainees);
    }

    @Test
    @DisplayName("Should retrieve an existent Trainee")
    void getEntityById()
    {
        Trainee trainee = new Trainee(null,
                "Larry", "Williams",
                null, null,
                true, LocalDate.parse("1991-03-21"), "123 Harlem St");

        Trainee traineeExpected = traineeService.saveEntity(trainee);

        UUID id = traineeExpected.getId();
        Trainee traineeActual = traineeService.getEntityById(id);

        String rawPassword = traineeExpected.getPassword();
        String encodedPassword = traineeActual.getPassword();

        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));

        traineeActual.setPassword(rawPassword);

        assertEquals(traineeExpected, traineeActual);
    }

    @Test
    @DisplayName("Should return null for non existent Trainee")
    void getEntityById2()
    {
        UUID nonExistentId = UUID.randomUUID();
        Trainee trainee = traineeService.getEntityById(nonExistentId);
        assertNull(trainee);
    }

    @Test
    @DisplayName("Should retrieve an existent Trainee by username")
    void getUserByUsername1()
    {
        Trainee traineeExpected = traineeService.getAllEntities().get(2);

        String username = "Charlie.Brown";
        Trainee traineeActual = traineeService.getUserByUsername(username);

        assertEquals(traineeExpected, traineeActual);
    }

    @Test
    @DisplayName("Should return null for non existent Trainee by username")
    void getUserByUsername2()
    {
        String username = "Unknown.Unknown";
        Trainee trainee = traineeService.getUserByUsername(username);
        assertNull(trainee);
    }

    @Test
    @DisplayName("Should activate an inactive Trainee by username")
    void activateUser()
    {
        Trainee trainee = traineeService.getUserByUsername("Ethan.Davis");
        assertFalse(trainee.getIsActive());

        traineeService.activateUser("Ethan.Davis");

        trainee = traineeService.getUserByUsername("Ethan.Davis");
        assertTrue(trainee.getIsActive());
    }

    @Test
    @DisplayName("Should deactivate an active Trainee by username")
    void deactivateUser()
    {
        Trainee trainee = traineeService.getUserByUsername("Charlie.Brown");
        assertTrue(trainee.getIsActive());

        traineeService.deactivateUser("Charlie.Brown");

        trainee = traineeService.getUserByUsername("Charlie.Brown");
        assertFalse(trainee.getIsActive());
    }

    @Test
    @DisplayName("Should login return true when username and password match")
    void login()
    {
        Trainee trainee = new Trainee(null,
                "Natalie", "Reed",
                null, null,
                true, LocalDate.parse("1993-12-05"), "17 Sunset Blvd");

        trainee = traineeService.saveEntity(trainee);

        String username = trainee.getUsername();
        String password = trainee.getPassword();

        boolean logged = traineeService.login(username, password);

        assertTrue(logged);
    }

    @Test
    @DisplayName("Should login return false when username or password is incorrect")
    void login2()
    {
        Trainee trainee = new Trainee(null,
                "Marcus", "Hughes",
                null, null,
                true, LocalDate.parse("1989-08-17"), "88 Riverbank Lane");

        trainee = traineeService.saveEntity(trainee);

        String username = trainee.getUsername();
        String password = trainee.getPassword();

        boolean logged;

        logged = traineeService.login("Unknown.Unknown", password);
        assertFalse(logged);

        logged = traineeService.login(username, "regularPassword");
        assertFalse(logged);
    }
}