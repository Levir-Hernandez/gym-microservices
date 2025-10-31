package com.crm.gym.api.config.cucumber.trainer;

import com.crm.gym.api.config.cucumber.ScenarioCommonContext;
import com.crm.gym.api.dtos.trainer.TrainerCredentials;
import com.crm.gym.api.dtos.trainer.TrainerRegistrationRequest;
import com.crm.gym.api.dtos.trainer.TrainerRespDto;
import com.crm.gym.api.entities.Trainer;
import com.crm.gym.api.repositories.interfaces.TrainerRepository;
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
public class StepDefinitions_CreateTrainer
{
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TrainerRepository trainerRepository;

    @Autowired private ScenarioCommonContext scenarioCommonContext;

    private TrainerRegistrationRequest trainerRegistrationRequest;
    private TrainerRespDto trainerResponse;

    @Given("a trainer with first name {string}, last name {string} and {string} specialization")
    public void aTrainerWithFirstNameLastNameAndSpecialization(String firstname, String lastname, String specialization)
    {
        firstname = sanitizeString(firstname);
        lastname = sanitizeString(lastname);
        specialization = sanitizeString(specialization);

        when(trainerRepository.create(any()))
                .thenAnswer(inv -> {
                    Trainer trainerToSave = inv.getArgument(0);
                    trainerToSave.setId(UUID.randomUUID());
                    return trainerToSave;
                });

        TrainerRegistrationRequest request = new TrainerRegistrationRequest();
        request.setFirstname(firstname);
        request.setLastname(lastname);
        request.setSpecialization(specialization);
        trainerRegistrationRequest = request;
    }

    @When("a request is made to create a trainer user")
    public void aRequestIsMadeToCreateATrainerUser() throws Exception
    {
        MvcResult result = mockMvc.perform(post("/trainers")
                        .with(user("Trainer.User").roles("TRAINER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerRegistrationRequest)))
                .andReturn();
        scenarioCommonContext.setResponseStatus(result.getResponse().getStatus());

        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode trainerNode = objectMapper.readTree(jsonResponse).path("trainer");
        trainerResponse = Optional.ofNullable(objectMapper.convertValue(
                trainerNode, new TypeReference<TrainerCredentials>(){})
        ).orElse(null);
    }

    @And("the generated trainer username should be {string}")
    public void theGeneratedTrainerUsernameShouldBe(String expectedTrainerUsername)
    {
        assertEquals(expectedTrainerUsername, trainerResponse.getUsername());
    }

    // Conversion Helpers

    private static String sanitizeString(String string)
    {
        return Optional.ofNullable(string)
                .filter(rv -> !rv.equalsIgnoreCase("N/A"))
                .orElse(null);
    }
}
