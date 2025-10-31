package com.crm.gym.api.config.cucumber.training;

import com.crm.gym.api.config.cucumber.ScenarioCommonContext;
import com.crm.gym.api.entities.Training;
import com.crm.gym.api.repositories.interfaces.TrainingRepository;
import com.crm.gym.client.reports.TrainerWorkloadClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@ScenarioScope
public class StepDefinitions_DeleteTraining
{
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TrainingRepository trainingRepository;
    @Autowired private TrainerWorkloadClient trainerWorkloadClient;

    @Autowired private ScenarioCommonContext scenarioCommonContext;

    @Given("a training session named {string} exists")
    public void a_training_session_named_exists(String trainingName)
    {
        Training training = new Training(trainingName);
        AtomicReference<Training> storedTraining = new AtomicReference<>(training);

        when(trainingRepository.findByName(trainingName))
                .thenAnswer(inv -> Optional.ofNullable(storedTraining.get()));

        when(trainingRepository.deleteByNameIfExists(trainingName)).thenAnswer(inv -> {
            storedTraining.set(null);
            return true;
        });
    }

    @Given("no training session named {string} exists")
    public void no_training_session_named_exists(String trainingName)
    {

    }

    @When("a request is made to delete the training session {string}")
    public void a_request_is_made_to_delete_the_training_session(String trainingName) throws Exception
    {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/trainings/{name}", trainingName)
                        .with(user("Trainer.User").roles("TRAINER"))
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        scenarioCommonContext.setResponseStatus(result.getResponse().getStatus());
    }
}
