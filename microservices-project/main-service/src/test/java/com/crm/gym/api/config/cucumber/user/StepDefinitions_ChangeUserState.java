package com.crm.gym.api.config.cucumber.user;

import com.crm.gym.api.config.cucumber.ScenarioCommonContext;
import com.crm.gym.api.entities.Trainee;
import com.crm.gym.api.entities.Trainer;
import com.crm.gym.api.entities.User;
import com.crm.gym.api.repositories.interfaces.TraineeRepository;
import com.crm.gym.api.repositories.interfaces.TrainerRepository;
import com.crm.gym.api.repositories.interfaces.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

@ScenarioScope
public class StepDefinitions_ChangeUserState
{
    @Autowired private MockMvc mockMvc;
    @Autowired private TraineeRepository traineeRepository;
    @Autowired private TrainerRepository trainerRepository;
    @Autowired private ObjectMapper objectMapper;

    @Autowired
    private ScenarioCommonContext scenarioCommonContext;

    private UserRepository<? extends User> userRepository;

    @Given("an {userState} {userType} named {string}")
    public void anStateUserNamed(Boolean userState, String userType, String username)
    {
        Function<String, User> getUser = resolveUserConstructor(userType);

        User trainee = getUser.apply(username);
        trainee.setIsActive(userState);

        AtomicReference<User> savedTrainee = new AtomicReference<>(trainee);

        userRepository = resolveUserRepository(userType);
        Mockito.when(userRepository.findByUsername(username))
                .thenAnswer(inv -> Optional.of(savedTrainee.get()));

        Mockito.when(userRepository.save(any()))
                .thenAnswer(inv -> {
                    User traineeToSave = inv.getArgument(0);
                    savedTrainee.set(traineeToSave);
                    return traineeToSave;
                });
    }

    @Given("no {userType} named {string}")
    public void noUserNamed(String userType, String username)
    {

    }

    @When("a request is made to {action} {userType} {string}")
    public void aRequestIsMadeToChangeUserState(String action, String userType, String username) throws Exception
    {
        MvcResult result = mockMvc.perform(patch("/"+userType+"s/{username}/" + action, username)
                .with(user("User.User").roles(userType.toUpperCase())))
                .andReturn();
        scenarioCommonContext.setResponseStatus(result.getResponse().getStatus());
    }

    // Conversion Helpers

    @ParameterType("activated|deactivated")
    public Boolean userState(String rawState)
    {
        return switch (rawState) {
            case "activated" -> true;
            case "deactivated" -> false;
            default -> null;
        };
    }

    @ParameterType("activate|deactivate")
    public String action(String action)
    {
        return action;
    }

    private Function<String, User> resolveUserConstructor(String userType)
    {
        return switch (userType) {
            case "trainee" -> Trainee::new;
            case "trainer" -> Trainer::new;
            default -> null;
        };
    }

    private UserRepository<? extends User> resolveUserRepository(String userType)
    {
        return switch (userType) {
            case "trainee" -> traineeRepository;
            case "trainer" -> trainerRepository;
            default -> null;
        };
    }
}