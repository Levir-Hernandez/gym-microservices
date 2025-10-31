package com.crm.gym.api.config.cucumber;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;
import io.cucumber.spring.ScenarioScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ScenarioScope
public class StepDefinitions_Common
{
    @Autowired private ScenarioCommonContext scenarioCommonContext;
    private Integer responseStatus;

    @Then("the response should be {responseStatus}")
    public void theResponseShouldBe(Integer expectedResponseStatus)
    {
        assertEquals(expectedResponseStatus, scenarioCommonContext.getResponseStatus());
    }

    @ParameterType("OK|Created|No Content|Bad Request|Unauthorized|Forbidden|Not Found")
    public Integer responseStatus(String rawResponseStatus)
    {
        return switch (rawResponseStatus) {
            case "OK"           -> HttpStatus.OK.value();           // 200
            case "Created"      -> HttpStatus.CREATED.value();      // 201
            case "No Content"   -> HttpStatus.NO_CONTENT.value();   // 204
            case "Bad Request"  -> HttpStatus.BAD_REQUEST.value();  // 400
            case "Unauthorized" -> HttpStatus.UNAUTHORIZED.value(); // 401
            case "Forbidden"    -> HttpStatus.FORBIDDEN.value();    // 403
            case "Not Found"    -> HttpStatus.NOT_FOUND.value();    // 404
            default             -> null;
        };
    }

    @ParameterType("traine[er]")
    public String userType(String userType)
    {
        return userType;
    }
}
