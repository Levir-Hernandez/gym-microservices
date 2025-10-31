package com.crm.gym.api.util;

import com.crm.gym.api.entities.Trainee;
import com.crm.gym.api.entities.Trainer;
import com.crm.gym.api.entities.TrainingType;
import com.crm.gym.api.repositories.interfaces.TraineeRepository;
import com.crm.gym.api.repositories.interfaces.TrainerRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = UsernameGeneratorImpl.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class UsernameGeneratorTest
{
    @Autowired private UsernameGenerator usernameGenerator;

    @MockitoBean private TraineeRepository traineeRepository;
    @MockitoBean private TrainerRepository trainerRepository;

    private static Trainee traineeMock;
    private static Trainer trainerMock;

    @BeforeAll
    static void beforeAll()
    {
        TrainingType trainingType = new TrainingType(UUID.randomUUID(), "Fitness");
        traineeMock = new Trainee(UUID.randomUUID(), "John", "Doe", null, null, true, LocalDate.parse("1990-06-15"), "123 Main St");
        trainerMock = new Trainer(UUID.randomUUID(), "John", "Doe", null, null, true, trainingType);
    }

    @Test
    @DisplayName("Should generate a username following 'firstname.lastname' format")
    void generateUsername()
    {
        usernameGenerator.setUser(traineeMock);

        String expectedUsername = "John.Doe";
        String actualUsername = usernameGenerator.generateUsername();
        assertEquals(expectedUsername, actualUsername);

        verify(traineeRepository).findByUsernameStartsWith(expectedUsername);
        verify(trainerRepository).findByUsernameStartsWith(expectedUsername);
    }

    @Test
    @DisplayName("Should append a serial number to usernames in case of collision")
    void generateUsername2()
    {
        Trainee traineeSavedMock1 = safeMutableMockOf(traineeMock);
        traineeSavedMock1.setUsername("John.Doe");

        Trainee traineeSavedMock2 = safeMutableMockOf(traineeMock);
        traineeSavedMock2.setUsername("John.Doe1");

        String expectedUsername = "John.Doe";
        when(traineeRepository.findByUsernameStartsWith(expectedUsername))
                .thenReturn(List.of()) // No collisions
                .thenReturn(List.of(traineeSavedMock1)) // One collision
                .thenReturn(List.of(traineeSavedMock1, traineeSavedMock2)); // Two collisions

        usernameGenerator.setUser(traineeMock);
        String actualUsername;

        // No collisions
        actualUsername = usernameGenerator.generateUsername();
        assertEquals(expectedUsername, actualUsername);

        // One collision
        actualUsername = usernameGenerator.generateUsername();
        assertEquals(expectedUsername+"1", actualUsername);

        // Two collisions
        actualUsername = usernameGenerator.generateUsername();
        assertEquals(expectedUsername+"2", actualUsername);

        verify(traineeRepository, times(3)).findByUsernameStartsWith(expectedUsername);
        verify(trainerRepository, times(3)).findByUsernameStartsWith(expectedUsername);
    }

    @Test
    @DisplayName("Should apply the same collision resolution for all user types (Trainees and Trainers)")
    void generateUsername3()
    {
        Trainee traineeSavedMock1 = safeMutableMockOf(traineeMock);
        traineeSavedMock1.setUsername("John.Doe");

        Trainer trainerSavedMock1 = safeMutableMockOf(trainerMock);
        trainerSavedMock1.setUsername("John.Doe1");

        String expectedUsername = "John.Doe";
        when(traineeRepository.findByUsernameStartsWith(expectedUsername))
                .thenReturn(List.of()) // No trainee collisions
                .thenReturn(List.of(traineeSavedMock1)) // One trainee collision
                .thenReturn(List.of(traineeSavedMock1)); // One trainee collision

        when(trainerRepository.findByUsernameStartsWith(expectedUsername))
                .thenReturn(List.of()) // No trainer collisions
                .thenReturn(List.of()) // No trainer collisions
                .thenReturn(List.of(trainerSavedMock1)); // One trainer collision

        usernameGenerator.setUser(traineeMock);
        String actualUsername;

        // No user collisions
        actualUsername = usernameGenerator.generateUsername();
        assertEquals(expectedUsername, actualUsername);

        // One user collision
        actualUsername = usernameGenerator.generateUsername();
        assertEquals(expectedUsername+"1", actualUsername);

        // Two user collisions
        actualUsername = usernameGenerator.generateUsername();
        assertEquals(expectedUsername+"2", actualUsername);

        verify(traineeRepository, times(3)).findByUsernameStartsWith(expectedUsername);
        verify(trainerRepository, times(3)).findByUsernameStartsWith(expectedUsername);
    }

    private Trainee safeMutableMockOf(Trainee trainee)
    {
        return new Trainee(trainee.getId(), trainee.getFirstname(), trainee.getLastname(), trainee.getUsername(), trainee.getPassword(), trainee.getIsActive(), trainee.getBirthdate(), trainee.getAddress());
    }

    private Trainer safeMutableMockOf(Trainer trainer)
    {
        return new Trainer(trainer.getId(), trainer.getFirstname(), trainer.getLastname(), trainer.getUsername(), trainer.getPassword(), trainer.getIsActive(), trainer.getSpecialization());
    }
}