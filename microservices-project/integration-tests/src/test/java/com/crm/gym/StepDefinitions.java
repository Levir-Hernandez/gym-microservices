package com.crm.gym;

import com.crm.gym.dao.TrainerRegistrationRequest;
import com.crm.gym.dao.TrainingScheduleRequest;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.time.LocalDate;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ScenarioScope
@Testcontainers
public class StepDefinitions
{
    @Container
    private static final DockerComposeContainer<?> environment =
            new DockerComposeContainer<>(new File("../docker-compose.yml"))
                    .waitingFor("activemq-broker", Wait.forHealthcheck())
                    .waitingFor("postgres-db", Wait.forHealthcheck())
                    .waitingFor("redis-db", Wait.forHealthcheck())
                    .waitingFor("mongo-db", Wait.forHealthcheck())
                    .withExposedService("main-service", 8080, Wait.forListeningPort())
                    .withExposedService("reports-service", 8081, Wait.forListeningPort())
                    .withRemoveVolumes(true);

    private static int mainPort;
    private static int reportsPort;

    private String accessToken;
    private Response lastResponse;

    @BeforeAll
    public static void initEnvironment()
    {
        environment.start();
        mainPort = environment.getServicePort("main-service", 8080);
        reportsPort = environment.getServicePort("reports-service", 8081);
    }

    @Given("the main service is up and running")
    public void the_main_service_is_up_and_running()
    {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = mainPort;
    }

    @Given("the reports service is up and running")
    public void the_reports_service_is_up_and_running()
    {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = reportsPort;
    }

    @When("a trainer registration request is submitted with the following details:")
    public void a_trainer_registration_request_is_submitted_with_the_following_details(TrainerRegistrationRequest request)
    {
        lastResponse = given()
            .accept(ContentType.JSON)
            .body(request)
            .contentType(ContentType.JSON)
        .when()
            .post("/trainers")
        .then()
            .extract()
            .response();
    }

    @When("the trainer {string} creates a training session with the following details:")
    public void the_trainer_creates_a_training_session_with_the_following_details(String trainerUsername, TrainingScheduleRequest request)
    {
        request.setTrainerUsername(trainerUsername);

        lastResponse = given()
            .header("Authorization", "Bearer " + accessToken)
            .accept(ContentType.JSON)
            .body(request)
            .contentType(ContentType.JSON)
        .when()
            .post("/trainings")
        .then()
            .extract()
            .response();
    }

    @When("a workload report is requested for trainer {string}")
    public void a_workload_report_is_requested_for_trainer(String trainerUsername)
    {
        lastResponse = given()
            .header("Authorization", "Bearer " + accessToken)
            .accept(ContentType.JSON)
        .when()
            .get("/trainers/{trainerUsername}/workloads", trainerUsername)
        .then()
            .extract()
            .response();
    }

    @And("the trainer {string} session has expired")
    public void theTrainerSessionExpire(String trainerUsername)
    {
        lastResponse = given()
                .header("Authorization", "Bearer " + accessToken)
                .accept(ContentType.JSON)
                .when()
                .post("/trainers/logout")
                .then()
                .extract()
                .response();
    }

    @Then("the response should contain an access token")
    public void the_response_should_contain_an_access_token()
    {
        accessToken = lastResponse.path("accessToken");
        assertNotNull(accessToken);
    }

    @Then("the generated username should be {string}")
    public void the_generated_username_should_be(String expectedTrainerUsername)
    {
        String currentTrainerUsername = lastResponse.path("trainer.username");
        assertEquals(expectedTrainerUsername, currentTrainerUsername);
    }

    @Then("the workload summary for {int}-{int} should show a total duration of {int} minutes")
    public void the_workload_summary_should_show_total_duration(Integer year, Integer month, Integer expectedDuration)
    {
        Integer actualDuration = lastResponse.path("workloadSummary."+year+"."+month);
        assertEquals(expectedDuration, actualDuration);
    }

    @Then("the response status should be {status}")
    public void the_response_status_should_be(Integer expectedStatus)
    {
        assertEquals(expectedStatus, lastResponse.statusCode());
    }

    // Conversion Helpers

    @ParameterType("OK|Created|Forbidden")
    public Integer status(String rawStatus)
    {
        return switch (rawStatus)
        {
            case "OK" -> 200;
            case "Created" -> 201;
            case "Forbidden" -> 403;
            default -> null;
        };
    }

    @DataTableType
    public TrainerRegistrationRequest getTrainerTrainerRegistrationRequest(Map<String, String> dataTableRow)
    {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest();
        request.setFirstname(dataTableRow.get("FirstName"));
        request.setLastname(dataTableRow.get("LastName"));
        request.setSpecialization(dataTableRow.get("Specialization"));
        return request;
    }

    @DataTableType
    public TrainingScheduleRequest getTrainingScheduleRequest(Map<String, String> dataTableRow)
    {
        TrainingScheduleRequest request = new TrainingScheduleRequest();
        request.setName(dataTableRow.get("Training name"));
        request.setTrainingType(dataTableRow.get("Training type"));
        request.setDate(LocalDate.parse(dataTableRow.get("Training date")));
        request.setDuration(Integer.parseInt(dataTableRow.get("Training duration")));
        request.setTraineeUsername(dataTableRow.get("Trainee username"));
        return request;
    }
}