package com.crm.gym.api.config.cucumber.trainee;

import com.crm.gym.api.config.cucumber.ScenarioCommonContext;
import com.crm.gym.api.dtos.trainee.TraineeCredentials;
import com.crm.gym.api.dtos.trainee.TraineeRegistrationRequest;
import com.crm.gym.api.dtos.trainee.TraineeRespDto;
import com.crm.gym.api.entities.Trainee;
import com.crm.gym.api.repositories.interfaces.TraineeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ScenarioScope
public class StepDefinitions_CreateTrainee
{
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TraineeRepository traineeRepository;

    @Autowired private ScenarioCommonContext scenarioCommonContext;

    private TraineeRegistrationRequest traineeRegistrationRequest;
    private TraineeRespDto traineeResponse;

    @Given("a trainee with first name {string} and last name {string}")
    public void aTraineeWithFirstNameAndLastName(String firstname, String lastname)
    {
        firstname = sanitizeString(firstname);
        lastname = sanitizeString(lastname);

        when(traineeRepository.create(any()))
                .thenAnswer(inv -> {
                    Trainee traineeToSave = inv.getArgument(0);
                    traineeToSave.setId(UUID.randomUUID());
                    return traineeToSave;
                });

        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        request.setFirstname(firstname);
        request.setLastname(lastname);
        traineeRegistrationRequest = request;
    }

    @When("a request is made to create a trainee user")
    public void aRequestIsMadeToCreateATraineeUser() throws Exception
    {
        MvcResult result = mockMvc.perform(post("/trainees")
                        .with(user("Trainee.User").roles("TRAINEE"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeRegistrationRequest)))
                .andReturn();

        scenarioCommonContext.setResponseStatus(result.getResponse().getStatus());
        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode traineeNode = objectMapper.readTree(jsonResponse).path("trainee");
        traineeResponse = Optional.ofNullable(objectMapper.convertValue(
                traineeNode, new TypeReference<TraineeCredentials>(){})
        ).orElse(null);
    }

    @And("the generated trainee username should be {string}")
    public void theGeneratedTraineeUsernameShouldBe(String expectedTraineeUsername)
    {
        assertEquals(expectedTraineeUsername, traineeResponse.getUsername());
    }

    // Conversion Helpers

    private static String sanitizeString(String string)
    {
        return Optional.ofNullable(string)
                .filter(rv -> !rv.equalsIgnoreCase("N/A"))
                .orElse(null);
    }
}
