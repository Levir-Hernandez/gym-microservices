package com.crm.gym.api.controllers;

import com.crm.gym.api.dtos.training.TrainingScheduleRequest;
import com.crm.gym.api.entities.Trainee;
import com.crm.gym.api.entities.Trainer;
import com.crm.gym.api.entities.Training;
import com.crm.gym.api.entities.TrainingType;
import com.crm.gym.api.services.TrainingService;
import com.crm.gym.api.util.EntityResourceLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TrainingControllerTest
{
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private TrainingService trainingService;
    @MockitoBean private EntityResourceLoader entityResourceLoader;

    private static Training trainingMock;
    private static Page<Training> trainingsFoundMock;

    @BeforeAll
    static void beforeAll()
    {
        TrainingType trainingType = new TrainingType(UUID.randomUUID(), "Fitness");
        Trainee trainee = new Trainee(UUID.randomUUID(), "Alice", "Smith", "Alice.Smith", null, true, LocalDate.parse("1990-06-15"), "123 Main St");
        Trainer trainer = new Trainer(UUID.randomUUID(), "John", "Doe", "John.Doe", null, true, trainingType);
        trainingMock = new Training(UUID.randomUUID(), "Full Body Fitness", LocalDate.parse("2025-06-07"), 60, trainee, trainer, trainingType);
        trainingsFoundMock = new PageImpl<>(List.of(trainingMock));
    }

    @Test
    @DisplayName("Tests HTTP 201 & 400 on POST /trainings")
    @WithMockUser(username = "Trainee.User", roles = "TRAINEE")
    void createTraining() throws Exception
    {
        TrainingScheduleRequest trainingDto = new TrainingScheduleRequest();
        trainingDto.setName("Morning Fitness Blast");
        trainingDto.setTrainingType("Fitness");
        trainingDto.setDate(LocalDate.parse("2025-06-21"));
        trainingDto.setDuration(30);
        trainingDto.setTrainerUsername("John.Doe");
        trainingDto.setTraineeUsername("Alice.Smith");

        when(trainingService.saveEntity(any())).thenReturn(trainingMock);

        // 201 CREATED

        mockMvc.perform(post("/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingDto)))
                .andExpect(status().isCreated());

        verify(trainingService).saveEntity(any());

        // 400 BAD REQUEST

        trainingDto.setTraineeUsername(null);

        mockMvc.perform(post("/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Tests HTTP 200 on GET /trainings")
    @WithMockUser(username = "Trainee.User", roles = "TRAINEE")
    void getAllTrainings() throws Exception
    {
        when(trainingService.getAllEntities(any(Pageable.class)))
                .thenReturn(trainingsFoundMock);

        mockMvc.perform(MockMvcRequestBuilders.get("/trainings")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(trainingService).getAllEntities(any(Pageable.class));
    }

    @Test
    @DisplayName("Tests HTTP 200 on GET /trainees/{traineeUsername}/trainings")
    @WithMockUser(username = "Trainee.User", roles = "TRAINEE")
    void getTrainingsByTraineeUsernameAndCriteria() throws Exception
    {
        when(trainingService.getTrainingsByCriteria(any(), any()))
                .thenReturn(trainingsFoundMock);

        String trainerUsername = "John.Doe";
        String traineeUsername = "Alice.Smith";
        String trainingTypeName = "Fitness";

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/trainees/{traineeUsername}/trainings", traineeUsername)
                        .param("trainerUsername", trainerUsername)
                        .param("fromDate", "2025-01-01")
                        .param("toDate", "2025-12-31")
                        .param("trainingTypeName", trainingTypeName)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.trainings[*].trainerUsername", everyItem(equalTo(trainerUsername))))
                .andExpect(jsonPath("$._embedded.trainings[*].traineeUsername", everyItem(equalTo(traineeUsername))))
                .andExpect(jsonPath("$._embedded.trainings[*].trainingType", everyItem(equalTo(trainingTypeName))));

        verify(trainingService).getTrainingsByCriteria(any(), any());
    }

    @Test
    @DisplayName("Tests HTTP 200 on GET /trainers/{trainerUsername}/trainings")
    @WithMockUser(username = "Trainer.User", roles = "TRAINER")
    void getTrainingsByTrainerUsernameAndCriteria() throws Exception
    {
        when(trainingService.getTrainingsByCriteria(any(), any()))
                .thenReturn(trainingsFoundMock);

        String traineeUsername = "Alice.Smith";
        String trainerUsername = "John.Doe";
        String trainingTypeName = "Fitness";

        mockMvc.perform(MockMvcRequestBuilders.get("/trainers/{trainerUsername}/trainings", trainerUsername)
                        .param("traineeUsername", traineeUsername)
                        .param("fromDate", "2025-01-01")
                        .param("toDate", "2025-12-31")
                        .param("trainingTypeName", trainingTypeName)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.trainings[*].trainerUsername", everyItem(equalTo(trainerUsername))))
                .andExpect(jsonPath("$._embedded.trainings[*].traineeUsername", everyItem(equalTo(traineeUsername))))
                .andExpect(jsonPath("$._embedded.trainings[*].trainingType", everyItem(equalTo(trainingTypeName))));

        verify(trainingService).getTrainingsByCriteria(any(), any());
    }
}