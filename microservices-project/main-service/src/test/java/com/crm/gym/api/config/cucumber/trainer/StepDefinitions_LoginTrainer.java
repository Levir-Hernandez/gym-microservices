package com.crm.gym.api.config.cucumber.trainer;

import com.crm.gym.api.config.cucumber.ScenarioCommonContext;
import com.crm.gym.api.dtos.trainer.TrainerChangePasswordRequest;
import com.crm.gym.api.dtos.trainer.TrainerLoginRequest;
import com.crm.gym.api.entities.Trainer;
import com.crm.gym.api.repositories.interfaces.TrainerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ScenarioScope
public class StepDefinitions_LoginTrainer
{
    @Autowired private MockMvc mockMvc;
    @Autowired private TrainerRepository trainerRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private ScenarioCommonContext scenarioCommonContext;

    @Given("the following trainers' credentials:")
    public void theFollowingTrainersCredentials(List<Trainer> trainers)
    {
        trainers.forEach(trainer ->
                Mockito.when(trainerRepository.findByUsername(trainer.getUsername()))
                        .thenReturn(Optional.of(trainer))
        );
    }

    @When("a request is made to log in with trainer username {string} and password {string}")
    public void aRequestIsMadeToLogInWithUsernameAndPassword(String username, String password) throws Exception
    {
        TrainerLoginRequest request = new TrainerLoginRequest();
        request.setUsername(username); request.setPassword(password);
        MvcResult result = mockMvc.perform(post("/trainers/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        scenarioCommonContext.setResponseStatus(result.getResponse().getStatus());
    }

    @When("the following trainer change password request is made:")
    public void theFollowingChangePasswordRequestIsMade(TrainerChangePasswordRequest trainerChangePasswordRequest) throws Exception
    {
        MvcResult result = mockMvc.perform(put("/trainers/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerChangePasswordRequest)))
                .andReturn();
        scenarioCommonContext.setResponseStatus(result.getResponse().getStatus());
    }

    // Conversion Helpers

    @DataTableType
    public Trainer getTrainer(Map<String, String> dataTableRow)
    {
        Trainer trainer = new Trainer();
        trainer.setUsername(dataTableRow.get("Username"));
        trainer.setPassword(Optional.ofNullable(dataTableRow.get("Password")).
                map(passwordEncoder::encode).orElse(null));
        return trainer;
    }

    @DataTableType
    public TrainerChangePasswordRequest getTrainerChangePasswordRequest(Map<String, String> dataTableRow)
    {
        TrainerChangePasswordRequest request = new TrainerChangePasswordRequest();
        request.setUsername(dataTableRow.get("Username"));
        request.setOldPassword(dataTableRow.get("Old password"));
        request.setNewPassword(dataTableRow.get("New password"));
        return request;
    }
}
