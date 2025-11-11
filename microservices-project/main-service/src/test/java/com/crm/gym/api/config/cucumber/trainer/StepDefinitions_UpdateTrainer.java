package com.crm.gym.api.config.cucumber.trainer;

import com.crm.gym.api.config.cucumber.ScenarioCommonContext;
import com.crm.gym.api.dtos.mappers.interfaces.TrainerMapper;
import com.crm.gym.api.dtos.trainer.TrainerModificationRequest;
import com.crm.gym.api.entities.Trainer;
import com.crm.gym.api.repositories.interfaces.TrainerRepository;
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
public class StepDefinitions_UpdateTrainer
{
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TrainerRepository trainerRepository;
    @Autowired private TrainerMapper trainerMapper;

    @Autowired private ScenarioCommonContext scenarioCommonContext;

    private String trainerUsername;

    @Given("the following trainers exist:")
    public void theFollowingTrainersExist(List<Map<String, String>> dataTable)
    {
        List<Trainer> savedTrainers  = new ArrayList<>();
        dataTable.forEach(dataTableRow -> {
            Trainer trainer = trainerMapper.toEntity(getTrainerModificationRequest(dataTableRow));
            trainer.setUsername(sanitizeString(dataTableRow.get("Username")));
            savedTrainers.add(trainer);
        });

        savedTrainers.forEach(trainer ->
                when(trainerRepository.updateByUsername(eq(trainer.getUsername()), any())).thenAnswer(inv -> {
                    Trainer t = inv.getArgument(1);
                    t.setUsername(inv.getArgument(0));
                    t.setTrainings(List.of());
                    return t;
                })
        );
    }

    @When("the following update trainer request is made:")
    public void theFollowingUpdateTrainerRequestIsMade(TrainerModificationRequest trainerModificationRequest) throws Exception
    {
        MvcResult result = mockMvc.perform(put("/trainers/{username}", trainerUsername)
                        .with(user("Trainer.User").roles("TRAINER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerModificationRequest)))
                .andReturn();
        scenarioCommonContext.setResponseStatus(result.getResponse().getStatus());
    }

    // Conversion Helpers

    @DataTableType
    public TrainerModificationRequest getTrainerModificationRequest(Map<String, String> dataTableRow)
    {
        trainerUsername = sanitizeString(dataTableRow.get("Username"));

        TrainerModificationRequest request = new TrainerModificationRequest();

        String firstname = sanitizeString(dataTableRow.get("Firstname"));
        String lastname = sanitizeString(dataTableRow.get("Lastname"));
        String specialization = sanitizeString(dataTableRow.get("Specialization"));
        Boolean isActive = Optional.ofNullable(sanitizeString(dataTableRow.get("Status")))
                .map(Boolean::parseBoolean).orElse(null);

        request.setFirstname(firstname);
        request.setLastname(lastname);
        request.setSpecialization(specialization);
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
