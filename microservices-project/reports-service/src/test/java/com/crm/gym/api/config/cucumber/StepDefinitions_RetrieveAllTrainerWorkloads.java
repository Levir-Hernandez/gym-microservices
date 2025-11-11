package com.crm.gym.api.config.cucumber;

import com.crm.gym.api.entities.TrainerWorkloadSummary;
import com.crm.gym.api.repositories.TrainerWorkloadRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ScenarioScope
public class StepDefinitions_RetrieveAllTrainerWorkloads
{
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TrainerWorkloadRepository trainerWorkloadRepository;

    private List<TrainerWorkloadSummary> trainerWorkloadSummaries;

    @Given("multiple trainers exist")
    public void multiple_trainers_exist()
    {
        when(trainerWorkloadRepository.findAll()).thenReturn(List.of(
                new TrainerWorkloadSummary("John.Doe"),
                new TrainerWorkloadSummary("Jane.Smith"),
                new TrainerWorkloadSummary("Mike.Johnson")
        ));
    }

    @Given("no trainers exist")
    public void no_trainers_exist()
    {

    }

    @When("a request is made to retrieve all trainer workloads")
    public void a_request_is_made_to_retrieve_all_trainer_workloads() throws Exception
    {
        MvcResult result = mockMvc.perform(get("/trainers/workloads")
                        .with(user("Trainer.User").roles("TRAINER")))
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        trainerWorkloadSummaries = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
    }

    @Then("the response should include a list of all trainer workloads")
    public void the_response_should_include_a_list_of_all_trainer_workloads()
    {
        int expectedSize = trainerWorkloadRepository.findAll().size();
        assertEquals(expectedSize, trainerWorkloadSummaries.size());
    }

    @Then("the response should include an empty list")
    public void the_response_should_include_an_empty_list()
    {
        assertTrue(trainerWorkloadSummaries.isEmpty());
    }
}
