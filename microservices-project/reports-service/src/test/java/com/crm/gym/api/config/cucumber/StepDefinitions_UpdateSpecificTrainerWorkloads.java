package com.crm.gym.api.config.cucumber;

import com.crm.gym.api.dtos.ActionType;
import com.crm.gym.api.dtos.TrainerWorkloadRequest;
import com.crm.gym.api.entities.TrainerWorkloadSummary;
import com.crm.gym.api.repositories.TrainerWorkloadRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ScenarioScope
public class StepDefinitions_UpdateSpecificTrainerWorkloads
{
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TrainerWorkloadRepository trainerWorkloadRepository;

    Map<String, TrainerWorkloadSummary> trainerWorkloadSummaries;
    private TrainerWorkloadRequest trainerWorkloadRequest;
    private String trainerUsername;
    private Integer responseStatus;

    @Given("a trainer named {string} exists with the following workload summary:")
    public void aTrainerNamedExistsWithTheFollowingWorkloadSummary(String trainerUsername, DataTable dataTable)
    {
        trainerWorkloadSummaries = new HashMap<>();

        TrainerWorkloadSummary trainerWorkloadSummary = new TrainerWorkloadSummary(trainerUsername);
        trainerWorkloadSummary.setWorkloadSummary(getWorkloadSummary(dataTable));

        trainerWorkloadSummaries.put(trainerUsername, trainerWorkloadSummary);

        when(trainerWorkloadRepository.findByTrainerUsername(anyString()))
                .thenAnswer(inv -> {
                    String trainerUsernameToFind = inv.getArgument(0);
                    return trainerWorkloadSummaries.get(trainerUsernameToFind);
                });

        when(trainerWorkloadRepository.save(any()))
                .thenAnswer(inv -> {
                    TrainerWorkloadSummary trainerWorkloadSummaryToSave = inv.getArgument(0);
                    trainerWorkloadSummaries.put(trainerWorkloadSummaryToSave.getTrainerUsername(), trainerWorkloadSummaryToSave);
                    return trainerWorkloadSummaryToSave;
                });
    }

    @Given("the following workload update request:")
    public void theFollowingWorkloadUpdateRequest(TrainerWorkloadRequest trainerWorkloadRequest)
    {
        this.trainerWorkloadRequest = trainerWorkloadRequest;
        this.trainerUsername = trainerWorkloadRequest.getTrainerUsername();
    }

    @When("it is made to {string} workload for the trainer")
    public void itIsMadeToWorkloadForTheTrainer(String rawActionType) throws Exception
    {
        ActionType actionType = parseRawValue(rawActionType, ActionType::valueOf);
        trainerWorkloadRequest.setActionType(actionType);

        MvcResult result = mockMvc.perform(post("/trainers/workloads")
                        .with(user("Trainer.User").roles("TRAINER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trainerWorkloadRequest)))
                .andReturn();

        responseStatus = Optional.of(result)
                .map(MvcResult::getResponse)
                .map(MockHttpServletResponse::getStatus)
                .orElse(null);
    }

    @Then("the final workload for {int}-{int} should be {int} hours")
    public void theFinalWorkloadForShouldBeHours(int year, int month, int expectedTrainingDuration)
    {
        TrainerWorkloadSummary trainerWorkloadSummary = trainerWorkloadRepository.findByTrainerUsername(trainerUsername);

        Integer actualTrainingDuration = Optional.ofNullable(trainerWorkloadSummary)
                .map(TrainerWorkloadSummary::getWorkloadSummary)
                .map(annualWorkloadSummary -> annualWorkloadSummary.get(year))
                .map(monthlyWorkloadSummary -> monthlyWorkloadSummary.get(month))
                .orElse(0);

        assertEquals(expectedTrainingDuration, actualTrainingDuration);
    }

    @Then("a new workload summary should be created for {string}")
    public void aNewWorkloadSummaryShouldBeCreatedFor(String trainerUsername)
    {
        TrainerWorkloadSummary trainerWorkloadSummary = trainerWorkloadRepository.findByTrainerUsername(trainerUsername);
        assertNotNull(trainerWorkloadSummary);
    }

    @Then("the response should be Bad Request")
    public void theResponseShouldBeBadRequest()
    {
        assertEquals(400, responseStatus);
    }

    // Conversion Helpers

    @DataTableType
    public static TrainerWorkloadRequest getTrainerWorkloadRequest(Map<String, String> dataTableRow)
    {
        String trainerUsername = parseRawValue(dataTableRow.get("Trainer username"), String::valueOf);
        LocalDate trainingDate = parseRawValue(dataTableRow.get("Training date"), LocalDate::parse);
        Integer trainingDuration = parseRawValue(dataTableRow.get("Training duration"), Integer::parseInt);

        return new TrainerWorkloadRequest(
                trainerUsername,
                null, null, null,
                trainingDate,
                trainingDuration,
                null
        );
    }

    private static <T> T parseRawValue(String rawValue, Function<String, T> parser)
    {
        return Optional.ofNullable(rawValue)
                .filter(rv -> !rv.equalsIgnoreCase("N/A"))
                .map(parser)
                .orElse(null);
    }

    private static Map<Integer, Map<Integer, Integer>> getWorkloadSummary(DataTable dataTable)
    {
        Map<Integer, Map<Integer, Integer>> workloadSummary = new HashMap<>();
        dataTable.asMaps(String.class, Integer.class).forEach(tableRow -> {
            Integer year = tableRow.get("Year");
            Integer month = tableRow.get("Month");
            Integer trainingDuration = tableRow.get("Trainer Workload");

            workloadSummary.computeIfAbsent(year, HashMap::new)
                    .merge(month, trainingDuration, Integer::sum);
        });
        return workloadSummary;
    }
}
