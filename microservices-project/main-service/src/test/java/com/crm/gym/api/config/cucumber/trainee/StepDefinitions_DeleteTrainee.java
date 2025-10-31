package com.crm.gym.api.config.cucumber.trainee;

import com.crm.gym.api.config.cucumber.ScenarioCommonContext;
import com.crm.gym.api.entities.Trainee;
import com.crm.gym.api.repositories.interfaces.TraineeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@ScenarioScope
public class StepDefinitions_DeleteTrainee
{
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TraineeRepository traineeRepository;

    @Autowired private ScenarioCommonContext scenarioCommonContext;

    private String traineeUsername;

    @Given("a trainee named {string} exists")
    public void aTraineeNamedExists(String traineeUsername)
    {
        AtomicReference<Trainee> savedTrainee = new AtomicReference<>(new Trainee(traineeUsername));
        Mockito.when(traineeRepository.findByUsername(traineeUsername))
                .thenAnswer(inv -> Optional.ofNullable(savedTrainee.get()));

        Mockito.when(traineeRepository.deleteByUsernameIfExists(traineeUsername))
                .thenAnswer(inv -> {
                    savedTrainee.set(null);
                    return true;
                });
    }

    @When("a request is made to delete the trainee {string}")
    public void aRequestIsMadeToDeleteTheTrainee(String traineeUsername) throws Exception
    {
        MvcResult result = mockMvc.perform(delete("/trainees/{username}", traineeUsername)
                .with(user("Trainee.User").roles("TRAINEE")))
                .andReturn();
        scenarioCommonContext.setResponseStatus(result.getResponse().getStatus());
        this.traineeUsername = traineeUsername;
    }

    @Then("the trainee should no longer exists")
    public void theTraineeShouldNoLongerExists()
    {
        assertTrue(traineeRepository.findByUsername(traineeUsername).isEmpty());
    }
}
