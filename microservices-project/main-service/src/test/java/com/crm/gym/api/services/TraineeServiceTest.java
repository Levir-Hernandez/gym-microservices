package com.crm.gym.api.services;

import com.crm.gym.api.config.TraineeServiceTestConfig;
import com.crm.gym.api.entities.Trainee;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

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
@ContextConfiguration(classes = TraineeServiceTestConfig.class)
class TraineeServiceTest
{
    @Autowired private TraineeService traineeService;
    @Autowired private PasswordEncoder passwordEncoder;

    @MockitoBean private TraineeRepository traineeRepository;
    @MockitoBean private TrainerRepository trainerRepository;
    @Captor private ArgumentCaptor<Trainee> traineeCaptor;

    private static Trainee traineeMock;
    private static Trainee traineeSavedMock;
    private static Trainee traineeUpdatedMock;

    @BeforeAll
    static void beforeAll(@Autowired PasswordEncoder passwordEncoder)
    {
        UUID traineeMockId = UUID.randomUUID();

        Trainee unsavedTrainee = new Trainee(null,
                "Charlie", "Brown",
                null, null,
                true, LocalDate.parse("1991-03-21"), "123 Harlem St");

        String traineeMockEncodedPassword = passwordEncoder.encode("0123456789");

        Trainee savedTrainee = new Trainee(traineeMockId,
                "Charlie", "Brown",
                "Charlie.Brown", traineeMockEncodedPassword,
                true, LocalDate.parse("1991-03-21"), "123 Harlem St");

        Trainee updatedTrainee = new Trainee(traineeMockId,
                "Chuck", "Brownie",
                "Charlie.Brown", traineeMockEncodedPassword,
                true, LocalDate.parse("1993-12-05"), "17 Sunset Blvd");

        traineeMock = unsavedTrainee;
        traineeSavedMock = savedTrainee;
        traineeUpdatedMock = updatedTrainee;
    }

    @Test
    @DisplayName("Should generate (id,username,password) and save Trainee")
    void saveEntity()
    {
        when(traineeRepository.create(any())).thenAnswer(inv -> {
            // Simulates JPA behavior
            Trainee traineeToSave = safeMutableMockOf(inv.getArgument(0)); // managed copy
            traineeToSave.setId(UUID.randomUUID()); // auto-generated id
            return traineeToSave;
        });

        Trainee traineeResult = traineeService.saveEntity(traineeMock);

        assertNotNull(traineeResult.getId());
        assertNotNull(traineeResult.getUsername());
        assertNotNull(traineeResult.getPassword());

        // captures trainee before persistence to extract encoded password
        verify(traineeRepository).create(traineeCaptor.capture());

        String rawPassword = traineeResult.getPassword();
        String encodedPassword = traineeCaptor.getValue().getPassword();
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Test
    @DisplayName("Should update existing Trainee")
    void updateEntity()
    {
        UUID id = traineeSavedMock.getId();
        AtomicReference<Trainee> storedTrainee = new AtomicReference<>(traineeSavedMock);
        when(traineeRepository.findById(id))
                .thenAnswer(inv -> Optional.of(storedTrainee.get()));
        when(traineeRepository.update(id, traineeUpdatedMock)).thenAnswer(inv -> {
            storedTrainee.set(traineeUpdatedMock);
            return traineeUpdatedMock;
        });

        Trainee oldTrainee = traineeService.getEntityById(id);
        assertNotEquals(traineeUpdatedMock, oldTrainee);

        traineeService.updateEntity(id, traineeUpdatedMock);
        Trainee newTrainee = traineeService.getEntityById(id);

        assertNotEquals(oldTrainee, newTrainee);
        assertEquals(traineeUpdatedMock, newTrainee);

        verify(traineeRepository, times(2)).findById(id);
        verify(traineeRepository).update(id, traineeUpdatedMock);
    }

    @Test
    @DisplayName("Should not update non existing Trainee")
    void updateEntity2()
    {
        UUID nonExistentId = UUID.randomUUID();

        Trainee oldTrainee = traineeService.getEntityById(nonExistentId);
        assertNull(oldTrainee);

        traineeService.updateEntity(nonExistentId, traineeUpdatedMock);

        Trainee newTrainee = traineeService.getEntityById(nonExistentId);
        assertNull(newTrainee);

        verify(traineeRepository, times(2)).findById(nonExistentId);
        verify(traineeRepository).update(nonExistentId, traineeUpdatedMock);
    }

    @Test
    @DisplayName("Should update existing Trainee by username")
    void updateUserByUsername()
    {
        String traineeUpdatedMockUsername = traineeSavedMock.getUsername();
        AtomicReference<Trainee> storedTrainee = new AtomicReference<>(traineeSavedMock);
        when(traineeRepository.findByUsername(traineeUpdatedMockUsername))
                .thenAnswer(inv -> Optional.of(storedTrainee.get()));
        when(traineeRepository.updateByUsername(traineeUpdatedMockUsername, traineeUpdatedMock)).thenAnswer(inv -> {
            storedTrainee.set(traineeUpdatedMock);
            return traineeUpdatedMock;
        });

        Trainee oldTrainee = traineeService.getUserByUsername(traineeUpdatedMockUsername);
        assertNotEquals(traineeUpdatedMock, oldTrainee);

        traineeService.updateUserByUsername(traineeUpdatedMockUsername, traineeUpdatedMock);
        Trainee newTrainee = traineeService.getUserByUsername(traineeUpdatedMockUsername);

        assertNotEquals(oldTrainee, newTrainee);
        assertEquals(traineeUpdatedMock, newTrainee);

        verify(traineeRepository, times(2)).findByUsername(traineeUpdatedMockUsername);
        verify(traineeRepository).updateByUsername(traineeUpdatedMockUsername, traineeUpdatedMock);
    }

    @Test
    @DisplayName("Should not update non existing Trainee by username")
    void updateUserByUsername2()
    {
        String nonExistentUsername = "Unknown.Unknown";

        Trainee oldTrainee = traineeService.getUserByUsername(nonExistentUsername);
        assertNull(oldTrainee);

        traineeService.updateUserByUsername(nonExistentUsername, traineeUpdatedMock);

        Trainee newTrainee = traineeService.getUserByUsername(nonExistentUsername);
        assertNull(newTrainee);

        verify(traineeRepository, times(2)).findByUsername(nonExistentUsername);
        verify(traineeRepository).updateByUsername(nonExistentUsername, traineeUpdatedMock);
    }

    @Test
    @DisplayName("Should delete existing Trainee")
    void deleteEntity()
    {
        UUID traineeSavedMockId = traineeSavedMock.getId();
        AtomicReference<Trainee> storedTrainee = new AtomicReference<>(traineeSavedMock);
        when(traineeRepository.findById(traineeSavedMockId)).thenAnswer(inv ->
                Optional.ofNullable(storedTrainee.get())
        );
        when(traineeRepository.deleteIfExists(traineeSavedMockId)).thenAnswer(inv -> {
            storedTrainee.set(null);
            return true;
        });

        Trainee trainee = traineeService.getEntityById(traineeSavedMockId);
        assertNotNull(trainee);

        boolean deleted = traineeService.deleteEntity(traineeSavedMockId);
        assertTrue(deleted);

        trainee = traineeService.getEntityById(traineeSavedMockId);
        assertNull(trainee);

        verify(traineeRepository, times(2)).findById(traineeSavedMockId);
        verify(traineeRepository).deleteIfExists(traineeSavedMockId);
    }

    @Test
    @DisplayName("Should not delete non existing Trainee")
    void deleteEntity2()
    {
        UUID nonExistentId = UUID.randomUUID();
        when(traineeRepository.deleteIfExists(nonExistentId)).thenReturn(false);

        Trainee trainee = traineeService.getEntityById(nonExistentId);
        assertNull(trainee);

        boolean deleted = traineeService.deleteEntity(nonExistentId);
        assertFalse(deleted);

        trainee = traineeService.getEntityById(nonExistentId);
        assertNull(trainee);

        verify(traineeRepository, times(2)).findById(nonExistentId);
        verify(traineeRepository).deleteIfExists(nonExistentId);
    }

    @Test
    @DisplayName("Should delete existing Trainee by username")
    void deleteTraineeByUsername()
    {
        String traineeSavedMockUsername = traineeSavedMock.getUsername();
        AtomicReference<Trainee> storedTrainee = new AtomicReference<>(traineeSavedMock);
        when(traineeRepository.findByUsername(traineeSavedMockUsername)).thenAnswer(inv ->
                Optional.ofNullable(storedTrainee.get())
        );
        when(traineeRepository.deleteByUsernameIfExists(traineeSavedMockUsername)).thenAnswer(inv -> {
            storedTrainee.set(null);
            return true;
        });

        Trainee trainee = traineeService.getUserByUsername(traineeSavedMockUsername);
        assertNotNull(trainee);

        boolean deleted = traineeService.deleteTraineeByUsername(traineeSavedMockUsername);
        assertTrue(deleted);

        trainee = traineeService.getUserByUsername(traineeSavedMockUsername);
        assertNull(trainee);

        verify(traineeRepository, times(2)).findByUsername(traineeSavedMockUsername);
        verify(traineeRepository).deleteByUsernameIfExists(traineeSavedMockUsername);
    }

    @Test
    @DisplayName("Should not delete non existing Trainee by username")
    void deleteTraineeByUsername2()
    {
        String nonExistentUsername = "Unknown.Unknown";
        when(traineeRepository.deleteByUsernameIfExists(nonExistentUsername)).thenReturn(false);

        Trainee trainee = traineeService.getUserByUsername(nonExistentUsername);
        assertNull(trainee);

        boolean deleted = traineeService.deleteTraineeByUsername(nonExistentUsername);
        assertFalse(deleted);

        trainee = traineeService.getUserByUsername(nonExistentUsername);
        assertNull(trainee);

        verify(traineeRepository, times(2)).findByUsername(nonExistentUsername);
        verify(traineeRepository).deleteByUsernameIfExists(nonExistentUsername);
    }

    @Test
    @DisplayName("Should retrieve an existent Trainee")
    void getEntityById()
    {
        UUID traineeSavedMockId = traineeSavedMock.getId();
        when(traineeRepository.findById(traineeSavedMockId)).thenReturn(Optional.of(traineeSavedMock));

        Trainee traineeActual = traineeService.getEntityById(traineeSavedMockId);
        assertEquals(traineeSavedMock, traineeActual);

        verify(traineeRepository).findById(traineeSavedMockId);
    }

    @Test
    @DisplayName("Should return null for non existent Trainee")
    void getEntityById2()
    {
        UUID nonExistentId = UUID.randomUUID();
        Trainee trainee = traineeService.getEntityById(nonExistentId);
        assertNull(trainee);

        verify(traineeRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should retrieve an existent Trainee by username")
    void getUserByUsername1()
    {
        String traineeSavedMockUsername = traineeSavedMock.getUsername();
        when(traineeRepository.findByUsername(traineeSavedMockUsername)).thenReturn(Optional.of(traineeSavedMock));

        Trainee traineeActual = traineeService.getUserByUsername(traineeSavedMockUsername);
        assertEquals(traineeSavedMock, traineeActual);
        verify(traineeRepository).findByUsername(traineeSavedMockUsername);
    }

    @Test
    @DisplayName("Should return null for non existent Trainee by username")
    void getUserByUsername2()
    {
        String nonExistentUsername = "Unknown.Unknown";
        Trainee trainee = traineeService.getUserByUsername(nonExistentUsername);
        assertNull(trainee);

        verify(traineeRepository).findByUsername(nonExistentUsername);
    }

    @Test
    @DisplayName("Should activate an inactive Trainee by username")
    void activateUser()
    {
        Trainee traineeSavedMock = safeMutableMockOf(TraineeServiceTest.traineeSavedMock);
        traineeSavedMock.setIsActive(false);

        String traineeSavedMockUsername = traineeSavedMock.getUsername();
        when(traineeRepository.findByUsername(traineeSavedMockUsername)).thenReturn(Optional.of(traineeSavedMock));

        Trainee trainee = traineeService.getUserByUsername(traineeSavedMockUsername);
        assertFalse(trainee.getIsActive());

        traineeService.activateUser(traineeSavedMockUsername);

        trainee = traineeService.getUserByUsername(traineeSavedMockUsername);
        assertTrue(trainee.getIsActive());

        verify(traineeRepository, atLeast(2)).findByUsername(traineeSavedMockUsername);
        verify(traineeRepository).save(any());
    }

    @Test
    @DisplayName("Should deactivate an active Trainee by username")
    void deactivateUser()
    {
        Trainee traineeSavedMock = safeMutableMockOf(TraineeServiceTest.traineeSavedMock);
        traineeSavedMock.setIsActive(true);

        String traineeSavedMockUsername = traineeSavedMock.getUsername();
        when(traineeRepository.findByUsername(traineeSavedMockUsername)).thenReturn(Optional.of(traineeSavedMock));

        Trainee trainee = traineeService.getUserByUsername(traineeSavedMockUsername);
        assertTrue(trainee.getIsActive());

        traineeService.deactivateUser(traineeSavedMockUsername);

        trainee = traineeService.getUserByUsername(traineeSavedMockUsername);
        assertFalse(trainee.getIsActive());

        verify(traineeRepository, atLeast(2)).findByUsername(traineeSavedMockUsername);
        verify(traineeRepository).save(any());
    }

    @Test
    @DisplayName("Should login return true when username and password match")
    void login()
    {
        String traineeSavedMockUsername = traineeSavedMock.getUsername();
        String traineeSavedMockRawPassword = "0123456789";

        when(traineeRepository.findByUsername(traineeSavedMockUsername)).thenReturn(Optional.of(traineeSavedMock));

        boolean logged = traineeService.login(traineeSavedMockUsername, traineeSavedMockRawPassword);
        assertTrue(logged);
        verify(traineeRepository).findByUsername(traineeSavedMockUsername);
    }

    @Test
    @DisplayName("Should login return false when username or password is incorrect")
    void login2()
    {
        String traineeSavedMockUsername = traineeSavedMock.getUsername();
        String traineeSavedMockRawPassword = "0123456789";

        when(traineeRepository.findByUsername(traineeSavedMockUsername)).thenReturn(Optional.of(traineeSavedMock));

        boolean logged;

        logged = traineeService.login("Unknown.Unknown", traineeSavedMockRawPassword);
        assertFalse(logged);

        logged = traineeService.login(traineeSavedMockUsername, "regularPassword");
        assertFalse(logged);

        verify(traineeRepository, times(2)).findByUsername(any());
    }

    private Trainee safeMutableMockOf(Trainee trainee)
    {
        return new Trainee(trainee.getId(), trainee.getFirstname(), trainee.getLastname(), trainee.getUsername(), trainee.getPassword(), trainee.getIsActive(), trainee.getBirthdate(), trainee.getAddress());
    }
}