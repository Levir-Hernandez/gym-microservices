package com.crm.gym.api.config.cucumber.trainee;

import com.crm.gym.api.config.cucumber.ScenarioCommonContext;
import com.crm.gym.api.dtos.trainee.TraineeProfile;
import com.crm.gym.api.entities.Trainee;
import com.crm.gym.api.repositories.interfaces.TraineeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ScenarioScope
public class StepDefinitions_GetTrainee
{
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TraineeRepository traineeRepository;

    @Autowired private ScenarioCommonContext scenarioCommonContext;

    private List<TraineeProfile> traineesResponse;

    @Given("multiple trainee users exist")
    public void multipleTraineeUsersExist()
    {
        Trainee trainee1 = new Trainee("John.Doe");
        trainee1.setTrainings(List.of());

        Trainee trainee2 = new Trainee("Alice.Smith");
        trainee2.setTrainings(List.of());

        Mockito.when(traineeRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(trainee1, trainee2)));
    }

    @Given("no trainee users exist")
    public void noTraineeUsersExist()
    {
        Mockito.when(traineeRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
    }

    @Given("a trainee with username {string} exists")
    public void aTraineeWithUsernameExists(String traineeUsername)
    {
        Trainee trainee = new Trainee(traineeUsername);
        trainee.setTrainings(List.of());
        Mockito.when(traineeRepository.findByUsername(traineeUsername))
                .thenReturn(Optional.of(trainee));
    }

    @Given("no trainee with username {string} exists")
    public void noTraineeWithUsernameExists(String traineeUsername)
    {
        Mockito.when(traineeRepository.findByUsername(traineeUsername))
                .thenReturn(Optional.empty());
    }

    @When("a request is made to retrieve all trainees")
    public void aRequestIsMadeToRetrieveAllTrainees() throws Exception
    {
        MvcResult result = mockMvc.perform(get("/trainees")
                        .with(user("Trainee.User").roles("TRAINEE"))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        scenarioCommonContext.setResponseStatus(result.getResponse().getStatus());

        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode traineesNode = objectMapper.readTree(jsonResponse).path("_embedded").path("trainees");
        traineesResponse = Optional.ofNullable(objectMapper.convertValue(
                traineesNode, new TypeReference<List<TraineeProfile>>(){})
        ).orElse(List.of());
    }

    @When("a request is made to retrieve the trainee by username {string}")
    public void aRequestIsMadeToRetrieveTheTraineeByUsername(String traineeUsername) throws Exception
    {
        MvcResult result = mockMvc.perform(get("/trainees/{username}", traineeUsername)
                        .with(user("Trainee.User").roles("TRAINEE")))
                .andReturn();
        scenarioCommonContext.setResponseStatus(result.getResponse().getStatus());
    }

    @Then("the response should include a list of all trainees")
    public void theResponseShouldIncludeAListOfAllTrainees()
    {
        int expectedSize = traineeRepository.findAll(Pageable.unpaged()).getSize();
        assertEquals(expectedSize, traineesResponse.size());
    }

    @Then("the response should include an empty list of trainees")
    public void theResponseShouldIncludeAnEmptyList()
    {
        assertEquals(0, traineesResponse.size());
    }
}
