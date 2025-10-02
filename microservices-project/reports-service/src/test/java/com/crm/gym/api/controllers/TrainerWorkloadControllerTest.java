package com.crm.gym.api.controllers;

import com.crm.gym.api.dtos.ActionType;
import com.crm.gym.api.dtos.TrainerWorkloadRequest;
import com.crm.gym.api.entities.TrainerWorkloadSummary;
import com.crm.gym.api.services.TrainerWorkloadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TrainerWorkloadControllerTest
{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TrainerWorkloadService trainerWorkloadService;

    @Test
    @DisplayName("Should return all trainers' workloads")
    @WithMockUser(username = "Trainer.User", roles = {"TRAINER"})
    void shouldReturnAllWorkloads() throws Exception
    {
        List<TrainerWorkloadSummary> mockList = List.of(
                new TrainerWorkloadSummary("alice", "Alice", "Smith", true),
                new TrainerWorkloadSummary("bob", "Bob", "Jones", true)
        );

        when(trainerWorkloadService.getAllTrainersWorkloads()).thenReturn(mockList);

        mockMvc.perform(get("/trainers/workloads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].trainerUsername").value("alice"))
                .andExpect(jsonPath("$[1].trainerUsername").value("bob"));

        verify(trainerWorkloadService, times(1)).getAllTrainersWorkloads();
    }

    @Test
    @DisplayName("Should return workload for single trainer")
    @WithMockUser(username = "Trainer.User", roles = {"TRAINER"})
    void shouldReturnTrainerWorkloadByUsername() throws Exception
    {
        TrainerWorkloadSummary summary = new TrainerWorkloadSummary("alice", "Alice", "Smith", true);
        when(trainerWorkloadService.getTrainerWorkloadByUsername("alice")).thenReturn(summary);

        mockMvc.perform(get("/trainers/alice/workloads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainerUsername").value("alice"))
                .andExpect(jsonPath("$.trainerFirstname").value("Alice"));

        verify(trainerWorkloadService, times(1)).getTrainerWorkloadByUsername("alice");
    }

    @Test
    @DisplayName("Should increase trainer workload")
    @WithMockUser(username = "Trainer.User", roles = {"TRAINER"})
    void shouldIncreaseWorkload() throws Exception
    {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "alice", "Alice", "Smith", true,
                LocalDate.of(2025, 10, 1),
                3, ActionType.ADD
        );

        when(trainerWorkloadService.increaseTrainerWorkload(
                any(), any(), any(), anyBoolean(), any(), anyInt()
        )).thenReturn(new TrainerWorkloadSummary("alice", "Alice", "Smith", true));

        mockMvc.perform(post("/trainers/workloads")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(trainerWorkloadService, times(1)).increaseTrainerWorkload(
                "alice", "Alice", "Smith", true,
                LocalDate.of(2025, 10, 1), 3
        );
    }

    @Test
    @DisplayName("Should decrease trainer workload")
    @WithMockUser(username = "Trainer.User", roles = {"TRAINER"})
    void shouldDecreaseWorkload() throws Exception
    {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "alice", "Alice", "Smith", true,
                LocalDate.of(2025, 10, 1),
                2, ActionType.DELETE
        );

        when(trainerWorkloadService.decreaseTrainerWorkload(
                anyString(), any(), anyInt()
        )).thenReturn(new TrainerWorkloadSummary("alice", "Alice", "Smith", true));

        mockMvc.perform(post("/trainers/workloads")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(trainerWorkloadService, times(1)).decreaseTrainerWorkload(
                "alice", LocalDate.of(2025, 10, 1), 2
        );
    }

    @Test
    @DisplayName("Should handle non-existent trainer on decrease")
    @WithMockUser(username = "Trainer.User", roles = {"TRAINER"})
    void shouldHandleNonExistentTrainerOnDecrease() throws Exception
    {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "unknown", "Unknown", "User", true,
                LocalDate.now(), 5, ActionType.DELETE
        );

        when(trainerWorkloadService.decreaseTrainerWorkload(
                anyString(), any(), anyInt()
        )).thenReturn(null);

        mockMvc.perform(post("/trainers/workloads")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(trainerWorkloadService, times(1)).decreaseTrainerWorkload(
                "unknown", request.getTrainingDate(), 5
        );
    }
}