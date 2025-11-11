package com.crm.gym.api.config.cucumber.trainee;

import com.crm.gym.api.config.cucumber.ScenarioCommonContext;
import com.crm.gym.api.dtos.mappers.interfaces.TraineeMapper;
import com.crm.gym.api.dtos.trainee.TraineeModificationRequest;
import com.crm.gym.api.entities.Trainee;
import com.crm.gym.api.repositories.interfaces.TraineeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ScenarioScope
public class StepDefinitions_UpdateTrainee
{
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TraineeRepository traineeRepository;
    @Autowired private TraineeMapper traineeMapper;

    @Autowired private ScenarioCommonContext scenarioCommonContext;

    private String traineeUsername;

    @Given("the following trainees exist:")
    public void theFollowingTraineesExist(List<Map<String, String>> dataTable)
    {
        List<Trainee> savedTrainees  = new ArrayList<>();
        dataTable.forEach(dataTableRow -> {
            Trainee trainee = traineeMapper.toEntity(getTraineeModificationRequest(dataTableRow));
            trainee.setUsername(sanitizeString(dataTableRow.get("Username")));
            savedTrainees.add(trainee);
        });

        savedTrainees.forEach(trainee ->
                when(traineeRepository.updateByUsername(eq(trainee.getUsername()), any())).thenAnswer(inv -> {
                    Trainee t = inv.getArgument(1);
                    t.setUsername(inv.getArgument(0));
                    t.setTrainings(List.of());
                    return t;
                })
        );
    }

    @When("the following update trainee request is made:")
    public void theFollowingUpdateTraineeRequestIsMade(TraineeModificationRequest traineeModificationRequest) throws Exception
    {
        MvcResult result = mockMvc.perform(put("/trainees/{username}", traineeUsername)
                        .with(user("Trainee.User").roles("TRAINEE"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeModificationRequest)))
                .andReturn();
        scenarioCommonContext.setResponseStatus(result.getResponse().getStatus());
    }

    // Conversion Helpers

    @DataTableType
    public TraineeModificationRequest getTraineeModificationRequest(Map<String, String> dataTableRow)
    {
        traineeUsername = sanitizeString(dataTableRow.get("Username"));

        TraineeModificationRequest request = new TraineeModificationRequest();

        String firstname = sanitizeString(dataTableRow.get("Firstname"));
        String lastname = sanitizeString(dataTableRow.get("Lastname"));
        Boolean isActive = Optional.ofNullable(sanitizeString(dataTableRow.get("Status")))
                .map(Boolean::parseBoolean).orElse(null);

        request.setFirstname(firstname);
        request.setLastname(lastname);
        request.setIsActive(isActive);
        return request;
    }

    private static String sanitizeString(String string)
    {
        return Optional.ofNullable(string)
                .filter(rv -> !rv.equalsIgnoreCase("N/A"))
                .orElse(null);
    }
}
