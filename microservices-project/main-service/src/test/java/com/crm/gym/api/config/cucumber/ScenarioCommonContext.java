package com.crm.gym.api.config.cucumber;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class ScenarioCommonContext
{
    private Integer responseStatus;

    public Integer getResponseStatus() {return responseStatus;}
    public void setResponseStatus(Integer responseStatus) {this.responseStatus = responseStatus;}
}
