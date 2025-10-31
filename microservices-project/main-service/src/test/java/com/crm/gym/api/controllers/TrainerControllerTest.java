package com.crm.gym.api.controllers;

import com.crm.gym.api.dtos.trainer.TrainerChangePasswordRequest;
import com.crm.gym.api.dtos.trainer.TrainerLoginRequest;
import com.crm.gym.api.dtos.trainer.TrainerModificationEmbeddedRequest;
import com.crm.gym.api.dtos.trainer.TrainerModificationRequest;
import com.crm.gym.api.dtos.trainer.TrainerRegistrationRequest;
import com.crm.gym.api.entities.Trainer;
import com.crm.gym.api.repositories.interfaces.TrainerRepository;
import com.crm.gym.api.services.TrainerService;
import com.crm.gym.api.util.EntityResourceLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.AdditionalAnswers.returnsSecondArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TrainerControllerTest
{
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoSpyBean private TrainerService trainerService;
    @MockitoBean private TrainerRepository trainerRepository;
    @MockitoBean private EntityResourceLoader entityResourceLoader;

    @Test
    @DisplayName("Tests HTTP 201 & 400 on POST /trainers")
    @WithMockUser(username = "Trainer.User", roles = "TRAINER")
    void createTrainer() throws Exception
    {
        String trainerFirstname = "Larry";
        String trainerLastname = "Williams";

        TrainerRegistrationRequest trainerDto = new TrainerRegistrationRequest();
        trainerDto.setFirstname(trainerFirstname);
        trainerDto.setLastname(trainerLastname);
        trainerDto.setSpecialization("Fitness");

        when(trainerRepository.create(any())).thenAnswer(returnsFirstArg());

        // 201 CREATED

        mockMvc.perform(post("/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.trainer.username").value(trainerFirstname+"."+trainerLastname));

        verify(trainerRepository).create(any());

        // 400 BAD_REQUEST

        trainerDto.setFirstname(null);

        mockMvc.perform(post("/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Tests HTTP 200 on GET /trainers")
    @WithMockUser(username = "Trainer.User", roles = "TRAINER")
    void getAllTrainers() throws Exception
    {
        when(trainerService.getAllEntities(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/trainers")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Tests HTTP 200 & 404 on GET /trainers/{username}")
    @WithMockUser(username = "Trainer.User", roles = "TRAINER")
    void getTrainerByUsername() throws Exception
    {
        String username = "Alice.Smith";
        Trainer trainer = new Trainer(username);
        trainer.setFirstname("Alice");
        trainer.setLastname("Smith");
        trainer.setTrainings(List.of());

        when(trainerService.getUserByUsername(username)).thenReturn(trainer);

        // 200 OK
        mockMvc.perform(get("/trainers/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("Alice"))
                .andExpect(jsonPath("$.lastname").value("Smith"));

        // 404 NOT_FOUND

        username = "Unknown.Unknown";

        mockMvc.perform(get("/trainers/{username}", username))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Tests HTTP 200 & 400 on PUT /trainers/{username}")
    @WithMockUser(username = "Trainer.User", roles = "TRAINER")
    void updateTrainerByUsername() throws Exception
    {
        String username = "Diana.Miller";
        String newTrainerFirstname = "Dina";
        String newTrainerLastname = "Merrill";
        TrainerModificationRequest trainerDto = new TrainerModificationRequest();
        trainerDto.setFirstname(newTrainerFirstname);
        trainerDto.setLastname(newTrainerLastname);
        trainerDto.setSpecialization("Fitness");
        trainerDto.setIsActive(false);

        when(trainerRepository.updateByUsername(any(), any())).thenAnswer(inv -> {
            Trainer t = inv.getArgument(1);
            t.setUsername(inv.getArgument(0));
            t.setTrainings(List.of());
            return t;
        });

        // 200 OK
        mockMvc.perform(put("/trainers/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("Dina"))
                .andExpect(jsonPath("$.lastname").value("Merrill"));

        // 400 BAD_REQUEST

        trainerDto.setFirstname(null);

        mockMvc.perform(put("/trainers/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Tests HTTP 200 & 404 on PATCH /trainers/{username}/activate")
    @WithMockUser(username = "Trainer.User", roles = "TRAINER")
    void activateTrainer() throws Exception
    {
        String username = "Alice.Smith";

        // 200 OK

        when(trainerService.activateUser(username)).thenReturn(true);
        mockMvc.perform(patch("/trainers/{username}/activate", username))
                .andExpect(status().isOk());

        // 404 NOT_FOUND

        mockMvc.perform(patch("/trainers/{username}/activate", "Unknown.Unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Tests HTTP 200 & 404 on PATCH /trainers/{username}/deactivate")
    @WithMockUser(username = "Trainer.User", roles = "TRAINER")
    void deactivateTrainer() throws Exception
    {
        String username = "Alice.Smith";

        // 200 OK

        when(trainerService.deactivateUser(username)).thenReturn(true);
        mockMvc.perform(patch("/trainers/{username}/deactivate", username))
                .andExpect(status().isOk());

        // 404 NOT_FOUND

        mockMvc.perform(patch("/trainers/{username}/deactivate", "Unknown.Unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Tests HTTP 200 & 401 on POST /trainers/login")
    void login() throws Exception
    {
        when(trainerService.login("Trainer.User", "0123456789"))
                .thenReturn(true);

        when(trainerService.login("invalid.invalid", "invalid"))
                .thenReturn(false);

        TrainerLoginRequest request = new TrainerLoginRequest();
        request.setUsername("Trainer.User");
        request.setPassword("0123456789");

        // 200 OK
        mockMvc.perform(post("/trainers/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // 401 UNAUTHORIZED

        request.setUsername("invalid.invalid");
        request.setPassword("invalid");

        mockMvc.perform(post("/trainers/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Tests HTTP 200 & 401 on PUT trainers/change-password")
    void changePassword() throws Exception
    {
        when(trainerService.changePassword("Trainer.User",
                "0123456789", "abcdefghij"))
                .thenReturn(true);

        TrainerChangePasswordRequest trainerDto = new TrainerChangePasswordRequest();

        // 200 OK

        trainerDto.setUsername("Trainer.User");
        trainerDto.setOldPassword("0123456789");
        trainerDto.setNewPassword("abcdefghij");

        mockMvc.perform(put("/trainers/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerDto)))
                .andExpect(status().isOk());

        // 401 UNAUTHORIZED

        trainerDto.setUsername("invalid.invalid");

        mockMvc.perform(put("/trainers/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Tests HTTP 200 & 404 on GET /trainees/{traineeUsername}/trainers/unassigned")
    @WithMockUser(username = "Trainee.User", roles = "TRAINEE")
    void getAllUnassignedForTraineeByUsername() throws Exception
    {
        String traineeUsername = "Alice.Smith";

        List<Trainer> unassignedTrainers = List.of(
                new Trainer("Mike.Johnson"),
                new Trainer("Laura.Williams"),
                new Trainer("Larry.Williams")
        );

        doReturn(new PageImpl<>(unassignedTrainers))
                .when(trainerService)
                .getAllUnassignedForTraineeByUsername(eq(traineeUsername), any(Pageable.class));

        // 200 OK

        mockMvc.perform(get("/trainees/{traineeUsername}/trainers/unassigned", traineeUsername)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.trainers[*].username",
                        containsInAnyOrder("Mike.Johnson", "Laura.Williams", "Larry.Williams")));

        // 404 NOT_FOUND

        traineeUsername = "Unknown.Unknown";
        mockMvc.perform(get("/trainees/{traineeUsername}/trainers/unassigned", traineeUsername)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Tests HTTP 200 & 400 & 404 on PUT /trainees/{traineeUsername}/trainers/assigned")
    @WithMockUser(username = "Trainee.User", roles = "TRAINEE")
    void updateAssignedTrainersForTrainee() throws Exception
    {
        String traineeUsername = "Alice.Smith";

        TrainerModificationEmbeddedRequest trainerDto = new TrainerModificationEmbeddedRequest();
        trainerDto.setUsername("Jane.Smith");
        trainerDto.setFirstname("Jennifer");
        trainerDto.setLastname("Brown");
        trainerDto.setSpecialization("Fitness");
        trainerDto.setIsActive(false);

        Set<TrainerModificationEmbeddedRequest> trainerDtos = Set.of(trainerDto);

        doAnswer(returnsSecondArg())
                .when(trainerService)
                .updateAssignedTrainersForTrainee(eq("Alice.Smith"), anySet());

        mockMvc.perform(put("/trainees/{traineeUsername}/trainers/assigned", traineeUsername)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerDtos)))
                .andExpect(status().isOk());

        // 400 BAD_REQUEST

        trainerDto.setUsername(null);

        mockMvc.perform(put("/trainees/{traineeUsername}/trainers/assigned", traineeUsername)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerDtos)))
                .andExpect(status().isBadRequest());

        // 404 NOT_FOUND

        traineeUsername = "Unknown.Unknown";
        trainerDto.setUsername("Jane.Smith");

        mockMvc.perform(put("/trainees/{traineeUsername}/trainers/assigned", traineeUsername)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerDtos)))
                .andExpect(status().isNotFound());
    }
}