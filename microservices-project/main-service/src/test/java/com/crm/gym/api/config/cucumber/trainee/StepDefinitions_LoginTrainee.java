package com.crm.gym.api.config.cucumber.trainee;

import com.crm.gym.api.config.cucumber.ScenarioCommonContext;
import com.crm.gym.api.dtos.trainee.TraineeChangePasswordRequest;
import com.crm.gym.api.dtos.trainee.TraineeLoginRequest;
import com.crm.gym.api.entities.Trainee;
import com.crm.gym.api.repositories.interfaces.TraineeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ScenarioScope
public class StepDefinitions_LoginTrainee
{
    @Autowired private MockMvc mockMvc;
    @Autowired private TraineeRepository traineeRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private ScenarioCommonContext scenarioCommonContext;

    @Autowired private RedisTemplate<String, String> redisTemplate;
    @Autowired private ValueOperations<String, String> valueOperations;

    private static Map<String, Long> failedLoginAttempts;

    @BeforeAll
    public static void resetFailedLoginAttempts()
    {
        failedLoginAttempts = new HashMap<>();
    }

    @Given("the following trainees' credentials:")
    public void theFollowingTraineesCredentials(List<Trainee> trainees)
    {
        trainees.forEach(trainee ->
                when(traineeRepository.findByUsername(trainee.getUsername()))
                        .thenReturn(Optional.of(trainee))
        );
    }

    @And("none of the trainees are locked out")
    public void noneOfTheTraineesAreLockedOut()
    {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.get(anyString())).thenAnswer(inv -> {
            String username = inv.getArgument(0);
            return Optional.ofNullable(failedLoginAttempts.get(username)).map(String::valueOf).orElse(null);
        });

        when(valueOperations.increment(anyString())).thenAnswer(inv -> {
            String username = inv.getArgument(0);
            return failedLoginAttempts.merge(username, 1L, Long::sum);
        });

        when(redisTemplate.delete(anyString())).thenAnswer(inv -> {
            String username = inv.getArgument(0);
            return Optional.ofNullable(failedLoginAttempts.remove(username)).isPresent();
        });
    }

    @When("a request is made to log in with trainee username {string} and password {string}")
    public void aRequestIsMadeToLogInWithTraineeUsernameAndPassword(String username, String password) throws Exception
    {
        TraineeLoginRequest request = new TraineeLoginRequest();
        request.setUsername(username); request.setPassword(password);
        MvcResult result = mockMvc.perform(post("/trainees/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        scenarioCommonContext.setResponseStatus(result.getResponse().getStatus());
    }

    @When("the following trainee change password request is made:")
    public void theFollowingTraineeChangePasswordRequestIsMade(TraineeChangePasswordRequest traineeChangePasswordRequest) throws Exception
    {
        MvcResult result = mockMvc.perform(put("/trainees/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeChangePasswordRequest)))
                .andReturn();
        scenarioCommonContext.setResponseStatus(result.getResponse().getStatus());
    }

    // Conversion Helpers

    @DataTableType
    public Trainee getTrainee(Map<String, String> dataTableRow)
    {
        Trainee trainee = new Trainee();
        trainee.setUsername(dataTableRow.get("Username"));
        trainee.setPassword(Optional.ofNullable(dataTableRow.get("Password")).
                map(passwordEncoder::encode).orElse(null));
        return trainee;
    }

    @DataTableType
    public TraineeChangePasswordRequest getTraineeChangePasswordRequest(Map<String, String> dataTableRow)
    {
        TraineeChangePasswordRequest request = new TraineeChangePasswordRequest();
        request.setUsername(dataTableRow.get("Username"));
        request.setOldPassword(dataTableRow.get("Old password"));
        request.setNewPassword(dataTableRow.get("New password"));
        return request;
    }
}
