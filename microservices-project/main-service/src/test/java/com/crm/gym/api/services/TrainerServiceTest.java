package com.crm.gym.api.services;

import com.crm.gym.api.entities.Trainer;
import com.crm.gym.api.services.TrainerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TrainerServiceTest
{
    private TrainerService trainerService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public TrainerServiceTest(TrainerService trainerService, PasswordEncoder passwordEncoder)
    {
        this.trainerService = trainerService;
        this.passwordEncoder = passwordEncoder;
    }

    @Test
    @DisplayName("Should generate (id,username,password) and save Trainer")
    void saveEntity()
    {
        int totalTrainers = trainerService.getAllEntities().size();

        Trainer trainer = new Trainer(null,
                "Larry", "Williams",
                null, null,
                true, null);

        Trainer trainerResult = trainerService.saveEntity(trainer);

        assertNotNull(trainerResult.getId());
        assertNotNull(trainerResult.getUsername());
        assertNotNull(trainerResult.getPassword());

        int actualTrainers = trainerService.getAllEntities().size();
        assertEquals(totalTrainers+1, actualTrainers);
    }

    @Test
    @DisplayName("Should update existing Trainer")
    void updateEntity()
    {
        UUID id = trainerService.getUserByUsername("Larry.Williams").getId();
        Trainer newTrainerInfo = new Trainer(null,
                "Larry", "Williams",
                "Willy", "secret1234",
                false, null);

        Trainer oldTrainer = trainerService.getEntityById(id);

        assertNotEquals(newTrainerInfo, oldTrainer);

        trainerService.updateEntity(id, newTrainerInfo);
        Trainer newTrainer = trainerService.getEntityById(id);

        assertNotEquals(oldTrainer, newTrainer);
        assertEquals(newTrainerInfo, newTrainer);
    }

    @Test
    @DisplayName("Should not update non existing Trainer")
    void updateEntity2()
    {
        int totalTrainers = trainerService.getAllEntities().size();
        UUID nonExistentId = UUID.randomUUID();

        Trainer newTrainerInfo = new Trainer(null,
                "Larry", "Williams",
                "Willy", "secret1234",
                false, null);

        Trainer oldTrainer = trainerService.getEntityById(nonExistentId);
        assertNull(oldTrainer);

        trainerService.updateEntity(nonExistentId, newTrainerInfo);

        Trainer newTrainer = trainerService.getEntityById(nonExistentId);
        assertNull(newTrainer);

        int actualTrainers = trainerService.getAllEntities().size();
        assertEquals(totalTrainers, actualTrainers);
    }

    @Test
    @DisplayName("Should update existing Trainer by username")
    void updateUserByUsername()
    {
        String username = "Larry.Williams";
        Trainer newTrainerInfo = new Trainer(null,
                "Larry", "Williams",
                "Willy", "secret1234",
                false, null);

        Trainer oldTrainer = trainerService.getUserByUsername(username);

        assertNotEquals(newTrainerInfo, oldTrainer);

        trainerService.updateUserByUsername(username, newTrainerInfo);
        Trainer newTrainer = trainerService.getUserByUsername(username);

        assertNotEquals(oldTrainer, newTrainer);
        assertEquals(newTrainerInfo, newTrainer);
    }

    @Test
    @DisplayName("Should not update non existing Trainer by username")
    void updateUserByUsername2()
    {
        int totalTrainers = trainerService.getAllEntities().size();
        String username = "Unknown.Unknown";

        Trainer newTrainerInfo = new Trainer(null,
                "Larry", "Williams",
                "Willy", "secret1234",
                false, null);

        Trainer oldTrainer = trainerService.getUserByUsername(username);
        assertNull(oldTrainer);

        trainerService.updateUserByUsername(username, newTrainerInfo);

        Trainer newTrainer = trainerService.getUserByUsername(username);
        assertNull(newTrainer);

        int actualTrainers = trainerService.getAllEntities().size();
        assertEquals(totalTrainers, actualTrainers);
    }

    @Test
    @DisplayName("Should retrieve an existent Trainer")
    void getEntityById()
    {
        Trainer trainer = new Trainer(null,
                "Larry", "Williams",
                null, null,
                true, null);

        Trainer trainerExpected = trainerService.saveEntity(trainer);

        UUID id = trainerExpected.getId();
        Trainer trainerActual = trainerService.getEntityById(id);

        String rawPassword = trainerExpected.getPassword();
        String encodedPassword = trainerActual.getPassword();

        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));

        trainerActual.setPassword(rawPassword);

        assertEquals(trainerExpected, trainerActual);
    }

    @Test
    @DisplayName("Should return null for non existent Trainer")
    void getEntityById2()
    {
        UUID nonExistentId = UUID.randomUUID();
        Trainer trainer = trainerService.getEntityById(nonExistentId);
        assertNull(trainer);
    }

    @Test
    @DisplayName("Should retrieve an existent Trainer by username")
    void getUserByUsername1()
    {
        Trainer trainerExpected = trainerService.getAllEntities().get(0);

        String username = "John.Doe";
        Trainer trainerActual = trainerService.getUserByUsername(username);

        assertEquals(trainerExpected, trainerActual);
    }

    @Test
    @DisplayName("Should return null for non existent Trainer by username")
    void getUserByUsername2()
    {
        String username = "Unknown.Unknown";
        Trainer trainer = trainerService.getUserByUsername(username);
        assertNull(trainer);
    }

    @Test
    @DisplayName("Should activate an inactive Trainer by username")
    void activateUser()
    {
        Trainer trainer = trainerService.getUserByUsername("Tom.Anderson");
        assertFalse(trainer.getIsActive());

        trainerService.activateUser("Tom.Anderson");

        trainer = trainerService.getUserByUsername("Tom.Anderson");
        assertTrue(trainer.getIsActive());
    }

    @Test
    @DisplayName("Should deactivate an active Trainer by username")
    void deactivateUser()
    {
        Trainer trainer = trainerService.getUserByUsername("Tom.Anderson");
        assertTrue(trainer.getIsActive());

        trainerService.deactivateUser("Tom.Anderson");

        trainer = trainerService.getUserByUsername("Tom.Anderson");
        assertFalse(trainer.getIsActive());
    }

    @Test
    @DisplayName("Should login return true when username and password match")
    void login()
    {
        Trainer trainer = new Trainer(null,
                "Derek", "Foster",
                null, null,
                false, null);

        trainer = trainerService.saveEntity(trainer);

        String username = trainer.getUsername();
        String password = trainer.getPassword();

        boolean logged = trainerService.login(username, password);

        assertTrue(logged);
    }

    @Test
    @DisplayName("Should login return false when username or password is incorrect")
    void login2()
    {
        Trainer trainer = new Trainer(null,
                "Ashley", "Johnson",
                null, null,
                false, null);

        trainer = trainerService.saveEntity(trainer);

        String username = trainer.getUsername();
        String password = trainer.getPassword();

        boolean logged;

        logged = trainerService.login("Unknown.Unknown", password);
        assertFalse(logged);

        logged = trainerService.login(username, "regularPassword");
        assertFalse(logged);
    }

    @Test
    @DisplayName("Should retrieve active Trainers unassigned to the given Trainee")
    void getAllUnassignedForTraineeByUsername()
    {
        String username = "Alice.Smith";

        List<Trainer> expectedUnassignedTrainers = List.of(
                trainerService.getUserByUsername("Mike.Johnson"),
                trainerService.getUserByUsername("Laura.Williams")
        );

        List<Trainer> actualUnassignedTrainers = trainerService.getAllUnassignedForTraineeByUsername(username);

        assertThat(actualUnassignedTrainers).containsExactlyInAnyOrderElementsOf(expectedUnassignedTrainers);
    }

    @Test
    @DisplayName("Should update multiple Trainers assigned to a Trainee")
    public void updateAssignedTrainersForTrainee()
    {
        String username = "Alice.Smith";
        Trainer trainer1, trainer2;

        trainer1 = trainerService.getUserByUsername("John.Doe");
        trainer2 = trainerService.getUserByUsername("Jane.Smith");

        assertNotEquals("Johnny", trainer1.getFirstname());
        assertNotEquals("Jennette", trainer2.getFirstname());

        trainer1.setFirstname("Johnny");
        trainer2.setFirstname("Jennette");

        Set<Trainer> trainersToUpdate = Set.of(trainer1, trainer2);
        trainerService.updateAssignedTrainersForTrainee(username, trainersToUpdate);

        trainer1 = trainerService.getUserByUsername("John.Doe");
        trainer2 = trainerService.getUserByUsername("Jane.Smith");

        assertEquals("Johnny", trainer1.getFirstname());
        assertEquals("Jennette", trainer2.getFirstname());
    }
}