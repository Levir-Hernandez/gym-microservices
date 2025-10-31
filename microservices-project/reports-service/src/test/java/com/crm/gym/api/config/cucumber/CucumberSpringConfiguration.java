package com.crm.gym.api.config.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.crm.gym.api.repositories.TrainerWorkloadRepository;

@SpringBootTest
@AutoConfigureMockMvc
@CucumberContextConfiguration
public class CucumberSpringConfiguration
{
    @MockitoBean private TrainerWorkloadRepository trainerWorkloadRepository;
}