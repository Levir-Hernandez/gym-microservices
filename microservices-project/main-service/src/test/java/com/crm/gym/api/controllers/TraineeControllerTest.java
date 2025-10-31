package com.crm.gym.api.controllers;

import com.crm.gym.api.dtos.trainee.TraineeChangePasswordRequest;
import com.crm.gym.api.dtos.trainee.TraineeLoginRequest;
import com.crm.gym.api.dtos.trainee.TraineeModificationRequest;
import com.crm.gym.api.dtos.trainee.TraineeRegistrationRequest;
import com.crm.gym.api.entities.Trainee;
import com.crm.gym.api.repositories.interfaces.TraineeRepository;
import com.crm.gym.api.services.TraineeService;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
class TraineeControllerTest
{
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoSpyBean private TraineeService traineeService;
    @MockitoBean private TraineeRepository traineeRepository;
    @MockitoBean private EntityResourceLoader entityResourceLoader;

    @Test
    @DisplayName("Tests HTTP 201 & 400 on POST /trainees")
    @WithMockUser(username = "Trainee.User", roles = "TRAINEE")
    void createTrainee() throws Exception
    {
        String traineeFirstname = "Larry";
        String traineeLastname = "Williams";

        TraineeRegistrationRequest traineeDto = new TraineeRegistrationRequest();
        traineeDto.setFirstname(traineeFirstname);
        traineeDto.setLastname(traineeLastname);
        traineeDto.setBirthdate(LocalDate.parse("1991-03-21"));
        traineeDto.setAddress("123 Harlem St");

        when(traineeRepository.create(any())).thenAnswer(returnsFirstArg());

        // 201 CREATED

        mockMvc.perform(post("/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.trainee.username").value(traineeFirstname+"."+traineeLastname));

        verify(traineeRepository).create(any());

        // 400 BAD_REQUEST

        traineeDto.setFirstname(null);

        mockMvc.perform(post("/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Tests HTTP 200 on GET /trainees")
    @WithMockUser(username = "Trainee.User", roles = "TRAINEE")
    void getAllTrainees() throws Exception
    {
        when(traineeService.getAllEntities(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/trainees")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Tests HTTP 200 & 404 on GET /trainees/{username}")
    @WithMockUser(username = "Trainee.User", roles = "TRAINEE")
    void getTraineeByUsername() throws Exception
    {
        String username = "Alice.Smith";
        Trainee trainee = new Trainee(username);
        trainee.setFirstname("Alice");
        trainee.setLastname("Smith");
        trainee.setTrainings(List.of());

        when(traineeService.getUserByUsername(username)).thenReturn(trainee);

        // 200 OK
        mockMvc.perform(get("/trainees/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("Alice"))
                .andExpect(jsonPath("$.lastname").value("Smith"));

        // 404 NOT_FOUND

        username = "Unknown.Unknown";

        mockMvc.perform(get("/trainees/{username}", username))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Tests HTTP 200 & 400 on PUT /trainees/{username}")
    @WithMockUser(username = "Trainee.User", roles = "TRAINEE")
    void updateTraineeByUsername() throws Exception
    {
        String username = "Diana.Miller";
        String newTraineeFirstname = "Dina";
        String newTraineeLastname = "Merrill";
        TraineeModificationRequest traineeDto = new TraineeModificationRequest();
        traineeDto.setFirstname(newTraineeFirstname);
        traineeDto.setLastname(newTraineeLastname);
        traineeDto.setBirthdate(LocalDate.parse("1995-04-19"));
        traineeDto.setAddress("123 Magnolia St");
        traineeDto.setIsActive(false);

        when(traineeRepository.updateByUsername(any(), any())).thenAnswer(inv -> {
            Trainee t = inv.getArgument(1);
            t.setUsername(inv.getArgument(0));
            t.setTrainings(List.of());
            return t;
        });

        // 200 OK
        mockMvc.perform(put("/trainees/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("Dina"))
                .andExpect(jsonPath("$.lastname").value("Merrill"));

        // 400 BAD_REQUEST

        traineeDto.setFirstname(null);

        mockMvc.perform(put("/trainees/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Tests HTTP 200 & 404 on PATCH /trainees/{username}/activate")
    @WithMockUser(username = "Trainee.User", roles = "TRAINEE")
    void activateTrainee() throws Exception
    {
        String username = "Alice.Smith";

        // 200 OK

        when(traineeService.activateUser(username)).thenReturn(true);
        mockMvc.perform(patch("/trainees/{username}/activate", username))
                .andExpect(status().isOk());

        // 404 NOT_FOUND

        mockMvc.perform(patch("/trainees/{username}/activate", "Unknown.Unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Tests HTTP 200 & 404 on PATCH /trainees/{username}/deactivate")
    @WithMockUser(username = "Trainee.User", roles = "TRAINEE")
    void deactivateTrainee() throws Exception
    {
        String username = "Alice.Smith";

        // 200 OK

        when(traineeService.deactivateUser(username)).thenReturn(true);
        mockMvc.perform(patch("/trainees/{username}/deactivate", username))
                .andExpect(status().isOk());

        // 404 NOT_FOUND

        mockMvc.perform(patch("/trainees/{username}/deactivate", "Unknown.Unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Tests HTTP 204 & 404 on DELETE /trainees/{username}")
    @WithMockUser(username = "Trainee.User", roles = "TRAINEE")
    void deleteTraineeByUsername() throws Exception
    {
        String username = "Ethan.Davis";
        when(traineeService.deleteTraineeByUsername(username)).thenReturn(true);

        // 204 NO_CONTENT
        mockMvc.perform(delete("/trainees/{username}", username))
                .andExpect(status().isNoContent());

        // 404 NOT_FOUND
        when(traineeService.deleteTraineeByUsername("Unknown.Unknown")).thenReturn(false);
        mockMvc.perform(delete("/trainees/{username}", "Unknown.Unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Tests HTTP 200 & 401 on POST /trainees/login")
    void login() throws Exception
    {
        when(traineeService.login("Trainee.User", "0123456789"))
                .thenReturn(true);

        when(traineeService.login("invalid.invalid", "invalid"))
                .thenReturn(false);

        TraineeLoginRequest request = new TraineeLoginRequest();
        request.setUsername("Trainee.User");
        request.setPassword("0123456789");

        // 200 OK
        mockMvc.perform(post("/trainees/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // 401 UNAUTHORIZED

        request.setUsername("invalid.invalid");
        request.setPassword("invalid");

        mockMvc.perform(post("/trainees/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Tests HTTP 200 & 401 on PUT trainees/change-password")
    void changePassword() throws Exception
    {
        when(traineeService.changePassword("Trainee.User",
                "0123456789", "abcdefghij"))
                .thenReturn(true);

        TraineeChangePasswordRequest traineeDto = new TraineeChangePasswordRequest();

        // 200 OK

        traineeDto.setUsername("Trainee.User");
        traineeDto.setOldPassword("0123456789");
        traineeDto.setNewPassword("abcdefghij");

        mockMvc.perform(put("/trainees/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeDto)))
                .andExpect(status().isOk());

        // 401 UNAUTHORIZED

        traineeDto.setUsername("invalid.invalid");

        mockMvc.perform(put("/trainees/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeDto)))
                .andExpect(status().isUnauthorized());
    }
}