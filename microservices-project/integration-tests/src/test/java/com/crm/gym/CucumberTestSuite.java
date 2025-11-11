package com.crm.gym;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectPackages("com.crm.gym")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.crm.gym")
public class CucumberTestSuite
{
    /**------------------------------------------------------------------ *
     *  This suite fails fast if no valid Docker environment is found     *
     * ------------------------------------------------------------------ *
     *  Windows: start Docker Desktop                                     *
     *  Linux: ensure Docker is running and user has proper access        *
     * ------------------------------------------------------------------ *
     *  Warning: It takes at least 1 min 15 sec to create the containers  *
     * -----------------------------------------------------------------**/
}