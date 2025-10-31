package com.crm.gym.api.config.cucumber;

import com.crm.gym.api.repositories.interfaces.TraineeRepository;
import com.crm.gym.api.repositories.interfaces.TrainerRepository;
import com.crm.gym.api.repositories.interfaces.TrainingRepository;
import com.crm.gym.client.reports.TrainerWorkloadClient;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@AutoConfigureMockMvc
@CucumberContextConfiguration
public class CucumberSpringConfiguration
{
    @MockitoBean private TraineeRepository traineeRepository;
    @MockitoBean private TrainerRepository trainerRepository;
    @MockitoBean private TrainingRepository trainingRepository;
    @MockitoBean private TrainerWorkloadClient trainerWorkloadClient;
}