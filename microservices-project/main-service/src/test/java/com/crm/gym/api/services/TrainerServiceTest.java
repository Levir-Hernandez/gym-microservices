package com.crm.gym.api.services;

import com.crm.gym.api.config.TrainerServiceTestConfig;
import com.crm.gym.api.entities.Trainer;
import com.crm.gym.api.entities.TrainingType;
import com.crm.gym.api.repositories.interfaces.TraineeRepository;
import com.crm.gym.api.repositories.interfaces.TrainerRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TrainerServiceTestConfig.class)
class TrainerServiceTest
{
    @Autowired private TrainerService trainerService;
    @Autowired private PasswordEncoder passwordEncoder;

    @MockitoBean private TrainerRepository trainerRepository;
    @MockitoBean private TraineeRepository traineeRepository;
    @Captor private ArgumentCaptor<Trainer> trainerCaptor;

    private static Trainer trainerMock;
    private static Trainer trainerSavedMock;
    private static Trainer trainerUpdatedMock;

    @BeforeAll
    static void beforeAll(@Autowired PasswordEncoder passwordEncoder)
    {
        TrainingType trainingType1 = new TrainingType(UUID.randomUUID(), "Fitness");
        TrainingType trainingType2 = new TrainingType(UUID.randomUUID(), "Yoga");

        UUID trainerMockId = UUID.randomUUID();

        Trainer unsavedTrainer = new Trainer(null,
                "Charlie", "Brown",
                null, null,
                true, trainingType1);

        String trainerMockEncodedPassword = passwordEncoder.encode("0123456789");

        Trainer savedTrainer = new Trainer(trainerMockId,
                "Charlie", "Brown",
                "Charlie.Brown", trainerMockEncodedPassword,
                true, trainingType1);

        Trainer updatedTrainer = new Trainer(trainerMockId,
                "Chuck", "Brownie",
                "Charlie.Brown", trainerMockEncodedPassword,
                true, trainingType2);

        trainerMock = unsavedTrainer;
        trainerSavedMock = savedTrainer;
        trainerUpdatedMock = updatedTrainer;
    }

    @Test
    @DisplayName("Should generate (id,username,password) and save Trainer")
    void saveEntity()
    {
        when(trainerRepository.create(any())).thenAnswer(inv -> {
            // Simulates JPA behavior
            Trainer trainerToSave = safeMutableMockOf(inv.getArgument(0)); // managed copy
            trainerToSave.setId(UUID.randomUUID()); // auto-generated id
            return trainerToSave;
        });

        Trainer trainerResult = trainerService.saveEntity(trainerMock);

        assertNotNull(trainerResult.getId());
        assertNotNull(trainerResult.getUsername());
        assertNotNull(trainerResult.getPassword());

        // captures trainer before persistence to extract encoded password
        verify(trainerRepository).create(trainerCaptor.capture());

        String rawPassword = trainerResult.getPassword();
        String encodedPassword = trainerCaptor.getValue().getPassword();
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Test
    @DisplayName("Should update existing Trainer")
    void updateEntity()
    {
        UUID id = trainerSavedMock.getId();
        AtomicReference<Trainer> storedTrainer = new AtomicReference<>(trainerSavedMock);
        when(trainerRepository.findById(id))
                .thenAnswer(inv -> Optional.of(storedTrainer.get()));
        when(trainerRepository.update(id, trainerUpdatedMock)).thenAnswer(inv -> {
            storedTrainer.set(trainerUpdatedMock);
            return trainerUpdatedMock;
        });

        Trainer oldTrainer = trainerService.getEntityById(id);
        assertNotEquals(trainerUpdatedMock, oldTrainer);

        trainerService.updateEntity(id, trainerUpdatedMock);
        Trainer newTrainer = trainerService.getEntityById(id);

        assertNotEquals(oldTrainer, newTrainer);
        assertEquals(trainerUpdatedMock, newTrainer);

        verify(trainerRepository, times(2)).findById(id);
        verify(trainerRepository).update(id, trainerUpdatedMock);
    }

    @Test
    @DisplayName("Should not update non existing Trainer")
    void updateEntity2()
    {
        UUID nonExistentId = UUID.randomUUID();

        Trainer oldTrainer = trainerService.getEntityById(nonExistentId);
        assertNull(oldTrainer);

        trainerService.updateEntity(nonExistentId, trainerUpdatedMock);

        Trainer newTrainer = trainerService.getEntityById(nonExistentId);
        assertNull(newTrainer);

        verify(trainerRepository, times(2)).findById(nonExistentId);
        verify(trainerRepository).update(nonExistentId, trainerUpdatedMock);
    }

    @Test
    @DisplayName("Should update existing Trainer by username")
    void updateUserByUsername()
    {
        String trainerUpdatedMockUsername = trainerSavedMock.getUsername();
        AtomicReference<Trainer> storedTrainer = new AtomicReference<>(trainerSavedMock);
        when(trainerRepository.findByUsername(trainerUpdatedMockUsername))
                .thenAnswer(inv -> Optional.of(storedTrainer.get()));
        when(trainerRepository.updateByUsername(trainerUpdatedMockUsername, trainerUpdatedMock)).thenAnswer(inv -> {
            storedTrainer.set(trainerUpdatedMock);
            return trainerUpdatedMock;
        });

        Trainer oldTrainer = trainerService.getUserByUsername(trainerUpdatedMockUsername);
        assertNotEquals(trainerUpdatedMock, oldTrainer);

        trainerService.updateUserByUsername(trainerUpdatedMockUsername, trainerUpdatedMock);
        Trainer newTrainer = trainerService.getUserByUsername(trainerUpdatedMockUsername);

        assertNotEquals(oldTrainer, newTrainer);
        assertEquals(trainerUpdatedMock, newTrainer);

        verify(trainerRepository, times(2)).findByUsername(trainerUpdatedMockUsername);
        verify(trainerRepository).updateByUsername(trainerUpdatedMockUsername, trainerUpdatedMock);
    }

    @Test
    @DisplayName("Should not update non existing Trainer by username")
    void updateUserByUsername2()
    {
        String nonExistentUsername = "Unknown.Unknown";

        Trainer oldTrainer = trainerService.getUserByUsername(nonExistentUsername);
        assertNull(oldTrainer);

        trainerService.updateUserByUsername(nonExistentUsername, trainerUpdatedMock);

        Trainer newTrainer = trainerService.getUserByUsername(nonExistentUsername);
        assertNull(newTrainer);

        verify(trainerRepository, times(2)).findByUsername(nonExistentUsername);
        verify(trainerRepository).updateByUsername(nonExistentUsername, trainerUpdatedMock);
    }

    @Test
    @DisplayName("Should retrieve an existent Trainer")
    void getEntityById()
    {
        UUID trainerSavedMockId = trainerSavedMock.getId();
        AtomicReference<Trainer> storedTrainer = new AtomicReference<>(trainerSavedMock);
        when(trainerRepository.findById(trainerSavedMockId)).thenAnswer(inv ->
                Optional.ofNullable(storedTrainer.get())
        );
        when(trainerRepository.deleteIfExists(trainerSavedMockId)).thenAnswer(inv -> {
            storedTrainer.set(null);
            return true;
        });

        Trainer trainer = trainerService.getEntityById(trainerSavedMockId);
        assertNotNull(trainer);

        boolean deleted = trainerService.deleteEntity(trainerSavedMockId);
        assertTrue(deleted);

        trainer = trainerService.getEntityById(trainerSavedMockId);
        assertNull(trainer);

        verify(trainerRepository, times(2)).findById(trainerSavedMockId);
        verify(trainerRepository).deleteIfExists(trainerSavedMockId);
    }

    @Test
    @DisplayName("Should return null for non existent Trainer")
    void getEntityById2()
    {
        UUID nonExistentId = UUID.randomUUID();
        Trainer trainer = trainerService.getEntityById(nonExistentId);
        assertNull(trainer);

        verify(trainerRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should retrieve an existent Trainer by username")
    void getUserByUsername1()
    {
        String trainerSavedMockUsername = trainerSavedMock.getUsername();
        when(trainerRepository.findByUsername(trainerSavedMockUsername)).thenReturn(Optional.of(trainerSavedMock));

        Trainer trainerActual = trainerService.getUserByUsername(trainerSavedMockUsername);
        assertEquals(trainerSavedMock, trainerActual);
        verify(trainerRepository).findByUsername(trainerSavedMockUsername);
    }

    @Test
    @DisplayName("Should return null for non existent Trainer by username")
    void getUserByUsername2()
    {
        String nonExistentUsername = "Unknown.Unknown";
        Trainer trainer = trainerService.getUserByUsername(nonExistentUsername);
        assertNull(trainer);

        verify(trainerRepository).findByUsername(nonExistentUsername);
    }

    @Test
    @DisplayName("Should activate an inactive Trainer by username")
    void activateUser()
    {
        Trainer trainerSavedMock = safeMutableMockOf(TrainerServiceTest.trainerSavedMock);
        trainerSavedMock.setIsActive(false);

        String trainerSavedMockUsername = trainerSavedMock.getUsername();
        when(trainerRepository.findByUsername(trainerSavedMockUsername)).thenReturn(Optional.of(trainerSavedMock));

        Trainer trainer = trainerService.getUserByUsername(trainerSavedMockUsername);
        assertFalse(trainer.getIsActive());

        trainerService.activateUser(trainerSavedMockUsername);

        trainer = trainerService.getUserByUsername(trainerSavedMockUsername);
        assertTrue(trainer.getIsActive());

        verify(trainerRepository, atLeast(2)).findByUsername(trainerSavedMockUsername);
        verify(trainerRepository).save(any());
    }

    @Test
    @DisplayName("Should deactivate an active Trainer by username")
    void deactivateUser()
    {
        Trainer trainerSavedMock = safeMutableMockOf(TrainerServiceTest.trainerSavedMock);
        trainerSavedMock.setIsActive(true);

        String trainerSavedMockUsername = trainerSavedMock.getUsername();
        when(trainerRepository.findByUsername(trainerSavedMockUsername)).thenReturn(Optional.of(trainerSavedMock));

        Trainer trainer = trainerService.getUserByUsername(trainerSavedMockUsername);
        assertTrue(trainer.getIsActive());

        trainerService.deactivateUser(trainerSavedMockUsername);

        trainer = trainerService.getUserByUsername(trainerSavedMockUsername);
        assertFalse(trainer.getIsActive());

        verify(trainerRepository, atLeast(2)).findByUsername(trainerSavedMockUsername);
        verify(trainerRepository).save(any());
    }

    @Test
    @DisplayName("Should login return true when username and password match")
    void login()
    {
        String trainerSavedMockUsername = trainerSavedMock.getUsername();
        String trainerSavedMockRawPassword = "0123456789";

        when(trainerRepository.findByUsername(trainerSavedMockUsername)).thenReturn(Optional.of(trainerSavedMock));

        boolean logged = trainerService.login(trainerSavedMockUsername, trainerSavedMockRawPassword);
        assertTrue(logged);
        verify(trainerRepository).findByUsername(trainerSavedMockUsername);
    }

    @Test
    @DisplayName("Should login return false when username or password is incorrect")
    void login2()
    {
        String trainerSavedMockUsername = trainerSavedMock.getUsername();
        String trainerSavedMockRawPassword = "0123456789";

        when(trainerRepository.findByUsername(trainerSavedMockUsername)).thenReturn(Optional.of(trainerSavedMock));

        boolean logged;

        logged = trainerService.login("Unknown.Unknown", trainerSavedMockRawPassword);
        assertFalse(logged);

        logged = trainerService.login(trainerSavedMockUsername, "regularPassword");
        assertFalse(logged);

        verify(trainerRepository, times(2)).findByUsername(any());
    }

    @Test
    @DisplayName("Should retrieve active Trainers unassigned to the given Trainee")
    void getAllUnassignedForTraineeByUsername()
    {
        Trainer trainerSavedMock1 = safeMutableMockOf(trainerSavedMock);
        trainerSavedMock1.setUsername("Mike.Johnson");

        Trainer trainerSavedMock2 = safeMutableMockOf(trainerSavedMock);
        trainerSavedMock2.setUsername("Laura.Williams");

        List<Trainer> trainersFoundMock = List.of(trainerSavedMock1, trainerSavedMock2);

        String traineeUsername = "Alice.Smith";
        when(traineeRepository.existsByUsername(traineeUsername)).thenReturn(true);
        when(trainerRepository.findAllUnassignedForTraineeByUsername(traineeUsername)).thenReturn(trainersFoundMock);

        List<Trainer> actualUnassignedTrainers = trainerService.getAllUnassignedForTraineeByUsername(traineeUsername);
        assertThat(actualUnassignedTrainers).containsExactlyInAnyOrderElementsOf(trainersFoundMock);

        verify(traineeRepository).existsByUsername(traineeUsername);
        verify(trainerRepository).findAllUnassignedForTraineeByUsername(traineeUsername);
    }

    @Test
    @DisplayName("Should update multiple Trainers assigned to a Trainee")
    public void updateAssignedTrainersForTrainee()
    {
        // ARRANGE MOCKS

        Trainer trainerSavedMock1 = safeMutableMockOf(TrainerServiceTest.trainerSavedMock);
        trainerSavedMock1.setFirstname("John"); trainerSavedMock1.setUsername("John.Doe");

        Trainer trainerSavedMock2 = safeMutableMockOf(TrainerServiceTest.trainerSavedMock);
        trainerSavedMock2.setFirstname("Jane"); trainerSavedMock2.setUsername("Jane.Smith");

        Map<String, Trainer> storedTrainers = new HashMap<>();
        storedTrainers.put(trainerSavedMock1.getUsername(), trainerSavedMock1);
        storedTrainers.put(trainerSavedMock2.getUsername(), trainerSavedMock2);

        when(trainerRepository.findByUsername(any())).thenAnswer(inv ->
                Optional.ofNullable(storedTrainers.get(inv.getArgument(0)))
                        .map(this::safeMutableMockOf)
        );

        Trainer trainerUpdatedMock1 = safeMutableMockOf(trainerSavedMock1);
        trainerUpdatedMock1.setFirstname("Johnny");

        Trainer trainerUpdatedMock2 = safeMutableMockOf(trainerSavedMock2);
        trainerUpdatedMock2.setFirstname("Jennette");

        String traineeUsername = "Alice.Smith";
        Set<Trainer> trainersToUpdate = Set.of(trainerUpdatedMock1, trainerUpdatedMock2);

        when(traineeRepository.existsByUsername(traineeUsername)).thenReturn(true);
        when(trainerRepository.updateAssignedTrainersForTrainee(traineeUsername, trainersToUpdate))
        .thenAnswer(inv -> {
            trainersToUpdate.stream()
                    .forEach(trainer -> storedTrainers.replace(trainer.getUsername(), trainer));
            return trainersToUpdate;
        });

        // ACT AND ASSERT

        Trainer trainer1, trainer2;
        trainer1 = trainerService.getUserByUsername("John.Doe");
        trainer2 = trainerService.getUserByUsername("Jane.Smith");

        assertEquals("John", trainer1.getFirstname());
        assertEquals("Jane", trainer2.getFirstname());

        trainerService.updateAssignedTrainersForTrainee(traineeUsername, trainersToUpdate);

        trainerUpdatedMock1 = trainerService.getUserByUsername("John.Doe");
        trainerUpdatedMock2 = trainerService.getUserByUsername("Jane.Smith");

        assertEquals("Johnny", trainerUpdatedMock1.getFirstname());
        assertEquals("Jennette", trainerUpdatedMock2.getFirstname());

        verify(traineeRepository).existsByUsername(traineeUsername);
        verify(trainerRepository, times(4)).findByUsername(any());
        verify(trainerRepository).updateAssignedTrainersForTrainee(traineeUsername, trainersToUpdate);
    }

    private Trainer safeMutableMockOf(Trainer trainer)
    {
        return new Trainer(trainer.getId(), trainer.getFirstname(), trainer.getLastname(), trainer.getUsername(), trainer.getPassword(), trainer.getIsActive(), trainer.getSpecialization());
    }
}