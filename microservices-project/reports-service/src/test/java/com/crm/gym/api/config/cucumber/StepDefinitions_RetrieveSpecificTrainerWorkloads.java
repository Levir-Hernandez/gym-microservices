package com.crm.gym.api.config.cucumber;

import com.crm.gym.api.entities.TrainerWorkloadSummary;
import com.crm.gym.api.repositories.TrainerWorkloadRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ScenarioScope
public class StepDefinitions_RetrieveSpecificTrainerWorkloads
{
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TrainerWorkloadRepository trainerWorkloadRepository;

    private Integer responseStatus;

    @Given("a trainer named {string} exists")
    public void a_trainer_named_exists(String trainerUsername)
    {
        when(trainerWorkloadRepository.findByTrainerUsername(trainerUsername))
                .thenReturn(new TrainerWorkloadSummary(trainerUsername));
    }

    @Given("no trainer named {string} exists")
    public void no_trainer_named_exists(String trainerUsername)
    {

    }

    @When("a request is made to retrieve workloads for {string}")
    public void a_request_is_made_to_retrieve_workloads_for(String trainerUsername) throws Exception
    {
        MvcResult result = mockMvc.perform(get("/trainers/{trainerUsername}/workloads", trainerUsername)
                        .with(user("Trainer.User").roles("TRAINER")))
                .andReturn();

        responseStatus = Optional.of(result)
                .map(MvcResult::getResponse)
                .map(MockHttpServletResponse::getStatus)
                .orElse(null);
    }

    @Then("the response should indicate that the trainer was found")
    public void the_response_should_indicate_that_the_trainer_was_found()
    {
        assertEquals(200, responseStatus);
    }

    @Then("the response should indicate that the trainer was not found")
    public void the_response_should_indicate_that_the_trainer_was_not_found()
    {
        assertEquals(404, responseStatus);
    }
}
