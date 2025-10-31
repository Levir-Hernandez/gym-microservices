package com.crm.gym.api.controllers;

import com.crm.gym.api.entities.TrainingType;
import com.crm.gym.api.repositories.interfaces.TrainingTypeRepository;
import com.crm.gym.api.util.EntityResourceLoader;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TrainingTypeControllerTest
{
    @Autowired private MockMvc mockMvc;
    @MockitoBean private TrainingTypeRepository trainingTypeRepository;
    @MockitoBean private EntityResourceLoader entityResourceLoader;

    private static Page<TrainingType> trainingTypesMock;

    @BeforeAll
    static void beforeAll()
    {
        TrainingType trainingType = new TrainingType(UUID.randomUUID(), "Fitness");
        trainingTypesMock = new PageImpl<>(List.of(trainingType));
    }

    @Test
    @DisplayName("Tests HTTP 200 on GET /trainingTypes")
    @WithMockUser(username = "Trainer.User", roles = {"TRAINER"})
    void getAllTrainingTypes() throws Exception
    {
        when(trainingTypeRepository.findAll(any(Pageable.class)))
                .thenReturn(trainingTypesMock);

        mockMvc.perform(get("/trainingTypes")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(trainingTypeRepository).findAll(any(Pageable.class));
    }
}