package com.crm.gym.api.config.cucumber;

import com.crm.gym.api.repositories.TrainerWorkloadRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ScenarioScope
public class StepDefinitions_PermissionControl
{
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TrainerWorkloadRepository trainerWorkloadRepository;

    private MockHttpServletRequestBuilder userRequest;
    private Integer responseStatus;

    @Given("any protected workload operation")
    public void any_protected_workload_operation() throws Exception
    {
        userRequest = get("/trainers/workloads");
    }

    @Given("the user is authenticated as {string}")
    public void the_user_is_authenticated_as(String userRole)
    {
        userRequest.with(user("User.User").roles(userRole));
    }

    @Given("the user is not authenticated")
    public void the_user_is_not_authenticated()
    {

    }

    @When("the user sends a request")
    public void the_user_sends_a_request() throws Exception
    {
        MvcResult result = mockMvc.perform(userRequest).andReturn();
        responseStatus = result.getResponse().getStatus();
    }

    @Then("the response should be OK")
    public void the_response_should_be_ok()
    {
        assertEquals(200, responseStatus);
    }

    @Then("the response should be Unauthorized")
    public void the_response_should_be_unauthorized()
    {
        assertEquals(401, responseStatus);
    }

    @Then("the response should be Forbidden")
    public void the_response_should_be_forbidden()
    {
        assertEquals(403, responseStatus);
    }
}
