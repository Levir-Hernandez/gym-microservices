package com.crm.gym.api.config.cucumber.trainer;

import com.crm.gym.api.config.cucumber.ScenarioCommonContext;
import com.crm.gym.api.dtos.trainer.TrainerProfile;
import com.crm.gym.api.entities.Trainer;
import com.crm.gym.api.repositories.interfaces.TrainerRepository;
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
public class StepDefinitions_GetTrainer
{
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TrainerRepository trainerRepository;

    @Autowired private ScenarioCommonContext scenarioCommonContext;

    private List<TrainerProfile> trainersResponse;

    @Given("multiple trainer users exist")
    public void multipleTrainerUsersExist()
    {
        Trainer trainer1 = new Trainer("John.Doe");
        trainer1.setTrainings(List.of());

        Trainer trainer2 = new Trainer("Alice.Smith");
        trainer2.setTrainings(List.of());

        Mockito.when(trainerRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(trainer1, trainer2)));
    }

    @Given("no trainer users exist")
    public void noTrainerUsersExist()
    {
        Mockito.when(trainerRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
    }

    @Given("a trainer with username {string} exists")
    public void aTrainerWithUsernameExists(String trainerUsername)
    {
        Trainer trainer = new Trainer(trainerUsername);
        trainer.setTrainings(List.of());
        Mockito.when(trainerRepository.findByUsername(trainerUsername))
                .thenReturn(Optional.of(trainer));
    }

    @Given("no trainer with username {string} exists")
    public void noTrainerWithUsernameExists(String trainerUsername)
    {
        Mockito.when(trainerRepository.findByUsername(trainerUsername))
                .thenReturn(Optional.empty());
    }

    @When("a request is made to retrieve all trainers")
    public void aRequestIsMadeToRetrieveAllTrainers() throws Exception
    {
        MvcResult result = mockMvc.perform(get("/trainers")
                        .with(user("Trainer.User").roles("TRAINER"))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        scenarioCommonContext.setResponseStatus(result.getResponse().getStatus());

        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode trainersNode = objectMapper.readTree(jsonResponse).path("_embedded").path("trainers");
        trainersResponse = Optional.ofNullable(objectMapper.convertValue(
                trainersNode, new TypeReference<List<TrainerProfile>>(){})
        ).orElse(List.of());
    }

    @When("a request is made to retrieve the trainer by username {string}")
    public void aRequestIsMadeToRetrieveTheTrainerByUsername(String trainerUsername) throws Exception
    {
        MvcResult result = mockMvc.perform(get("/trainers/{username}", trainerUsername)
                        .with(user("Trainer.User").roles("TRAINER")))
                .andReturn();
        scenarioCommonContext.setResponseStatus(result.getResponse().getStatus());
    }

    @Then("the response should include a list of all trainers")
    public void theResponseShouldIncludeAListOfAllTrainers()
    {
        int expectedSize = trainerRepository.findAll(Pageable.unpaged()).getSize();
        assertEquals(expectedSize, trainersResponse.size());
    }

    @Then("the response should include an empty list of trainers")
    public void theResponseShouldIncludeAnEmptyList()
    {
        assertEquals(0, trainersResponse.size());
    }
}
