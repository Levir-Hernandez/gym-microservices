package com.crm.gym.api.controllers;

import com.crm.gym.api.dtos.trainee.TraineeRegistrationRequest;
import com.crm.gym.api.dtos.trainee.TraineeTokenWrapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TrainingTypeControllerTest
{
    private static String ACCESS_TOKEN;

    @Autowired
    public TrainingTypeControllerTest(TraineeController traineeController)
    {
        TraineeRegistrationRequest traineeRegistrationRequest = new TraineeRegistrationRequest();
        traineeRegistrationRequest.setFirstname("user");
        traineeRegistrationRequest.setLastname("test");

        ACCESS_TOKEN = ((TraineeTokenWrapper) traineeController.createTrainee(traineeRegistrationRequest).getContent()).getAccessToken();
    }

    @BeforeAll
    static void beforeAll()
    {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    @DisplayName("Tests HTTP 200 on GET /trainingTypes")
    void getAllTrainingTypes()
    {
        given()
            .header("Authorization", "Bearer " + ACCESS_TOKEN)
            .accept(ContentType.JSON)
        .when()
            .get("/trainingTypes")
        .then()
            .statusCode(200);
    }
}