package com.crm.gym.api.config.cucumber.training;

import com.crm.gym.api.config.cucumber.ScenarioCommonContext;
import com.crm.gym.api.dtos.training.TrainingScheduleRequest;
import com.crm.gym.api.repositories.interfaces.TrainingRepository;
import com.crm.gym.client.reports.TrainerWorkloadClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ScenarioScope
public class StepDefinitions_CreateTraining
{
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TrainingRepository trainingRepository;
    @Autowired private TrainerWorkloadClient trainerWorkloadClient;

    @Autowired private ScenarioCommonContext scenarioCommonContext;

    private TrainingScheduleRequest trainingScheduleRequest;

    @Given("the following training session details:")
    public void the_following_training_session_details(TrainingScheduleRequest trainingScheduleRequest)
    {
        this.trainingScheduleRequest = trainingScheduleRequest;
        when(trainingRepository.create(any())).thenAnswer(returnsFirstArg());
    }

    @When("a request is made to create the training session")
    public void a_request_is_made_to_create_the_training_session() throws Exception
    {
        MvcResult result = mockMvc.perform(post("/trainings")
                .with(user("Trainer.User").roles("TRAINER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trainingScheduleRequest)))
                .andReturn();
        scenarioCommonContext.setResponseStatus(result.getResponse().getStatus());
    }

    // Conversion Helpers

    @DataTableType
    public TrainingScheduleRequest getTrainingScheduleRequest(Map<String, String> dataTableRow)
    {
        String trainingType = parseRawValue(dataTableRow.get("Training type"), String::valueOf);
        String name = parseRawValue(dataTableRow.get("Training name"), String::valueOf);
        LocalDate date = parseRawValue(dataTableRow.get("Training date"), LocalDate::parse);
        Integer duration = parseRawValue(dataTableRow.get("Training duration"), Integer::parseInt);
        String trainerUsername = parseRawValue(dataTableRow.get("Trainer username"), String::valueOf);
        String traineeUsername = parseRawValue(dataTableRow.get("Trainee username"), String::valueOf);

        TrainingScheduleRequest request = new TrainingScheduleRequest();
        request.setTrainingType(trainingType);
        request.setName(name);
        request.setDate(date);
        request.setDuration(duration);
        request.setTrainerUsername(trainerUsername);
        request.setTraineeUsername(traineeUsername);
        return request;
    }

    private static <T> T parseRawValue(String rawValue, Function<String, T> parser)
    {
        return Optional.ofNullable(rawValue)
                .filter(rv -> !rv.equalsIgnoreCase("N/A"))
                .map(parser)
                .orElse(null);
    }
}
