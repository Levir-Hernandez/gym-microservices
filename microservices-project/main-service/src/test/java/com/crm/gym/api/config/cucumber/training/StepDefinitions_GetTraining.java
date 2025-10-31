package com.crm.gym.api.config.cucumber.training;

import com.crm.gym.api.config.cucumber.ScenarioCommonContext;
import com.crm.gym.api.dtos.training.TrainingDetails;
import com.crm.gym.api.entities.Trainee;
import com.crm.gym.api.entities.Trainer;
import com.crm.gym.api.entities.Training;
import com.crm.gym.api.entities.User;
import com.crm.gym.api.repositories.TrainingQueryCriteria;
import com.crm.gym.api.repositories.interfaces.TrainingRepository;
import com.crm.gym.client.reports.TrainerWorkloadClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ScenarioScope
public class StepDefinitions_GetTraining
{
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TrainingRepository trainingRepository;
    @Autowired private TrainerWorkloadClient trainerWorkloadClient;

    @Autowired private ScenarioCommonContext scenarioCommonContext;

    private List<TrainingDetails> trainingsResponse;

    @Given("multiple training sessions exist")
    public void multiple_training_sessions_exist()
    {
        when(trainingRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(
                        new Training("Full Body Fitness"),
                        new Training("Morning Yoga Flow")
                )));
    }

    @Given("no training sessions exist")
    public void no_training_sessions_exist()
    {
        when(trainingRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
    }

    @Given("the {userType} {string} has trainings assigned")
    public void the_user_has_trainings_assigned(String userType, String username)
    {
        User user = getUserSupplier(userType).get();
        user.setUsername(username);

        Consumer<Training> setUser = training ->
        {
            if(user instanceof Trainee trainee) {training.setTrainee(trainee);}
            else if(user instanceof Trainer trainer) {training.setTrainer(trainer);}
        };

        Training training1 = new Training("Full Body Fitness");
        Training training2 = new Training("Morning Yoga Flow");

        setUser.accept(training1);
        setUser.accept(training2);

        when(trainingRepository.findByCriteria(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(training1, training2)));
    }

    @Given("the {userType} {string} has no trainings assigned")
    public void the_user_has_no_trainings_assigned(String userType, String username)
    {
        when(trainingRepository.findByCriteria(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
    }

    @When("a request is made to retrieve all training sessions")
    public void a_request_is_made_to_retrieve_all_training_sessions() throws Exception
    {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/trainings")
                        .with(SecurityMockMvcRequestPostProcessors.user("Trainer.User").roles("TRAINER"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        scenarioCommonContext.setResponseStatus(result.getResponse().getStatus());
        String jsonResponse = result.getResponse().getContentAsString();

        JsonNode trainingsNode = objectMapper.readTree(jsonResponse).path("_embedded").path("trainings");
        trainingsResponse = Optional.ofNullable(objectMapper.convertValue(
                    trainingsNode, new TypeReference<List<TrainingDetails>>(){})
                ).orElse(List.of());
    }

    @When("a request is made to retrieve trainings by {userType} username {string}")
    public void a_request_is_made_to_retrieve_trainings_by_user_username(String userType, String username) throws Exception
    {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/"+userType+"s/{username}/trainings", username)
                        .with(SecurityMockMvcRequestPostProcessors.user("User.User").roles(userType.toUpperCase()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        scenarioCommonContext.setResponseStatus(result.getResponse().getStatus());
        String jsonResponse = result.getResponse().getContentAsString();

        JsonNode trainingsNode = objectMapper.readTree(jsonResponse).path("_embedded").path("trainings");
        trainingsResponse = Optional.ofNullable(objectMapper.convertValue(
                trainingsNode, new TypeReference<List<TrainingDetails>>(){})
        ).orElse(List.of());
    }

    @Then("the response should include a list of all training sessions")
    public void the_response_should_include_a_list_of_all_training_sessions()
    {
        int expectedSize = trainingRepository.findAll(Pageable.unpaged()).getSize();
        assertEquals(expectedSize, trainingsResponse.size());
    }

    @Then("the response should include an empty list of training sessions")
    public void the_response_should_include_an_empty_list()
    {
        assertTrue(trainingsResponse.isEmpty());
    }

    @Then("the response should return all trainings assigned to {userType} {string}")
    public void the_response_should_return_all_trainings_assigned_to_user(String userType, String username)
    {
        int expectedSize = trainingRepository.findByCriteria(TrainingQueryCriteria.builder().build(), Pageable.unpaged()).getSize();
        assertEquals(expectedSize, trainingsResponse.size());
    }

    // Conversion Helpers

    private Supplier<User> getUserSupplier(String userType)
    {
        return switch (userType)
        {
            case "trainee" -> Trainee::new;
            case "trainer" -> Trainer::new;
            default -> null;
        };
    }
}
