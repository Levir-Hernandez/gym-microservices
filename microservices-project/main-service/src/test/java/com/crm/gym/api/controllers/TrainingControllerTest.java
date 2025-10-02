package com.crm.gym.api.controllers;

import com.crm.gym.api.dtos.trainee.TraineeRegistrationRequest;
import com.crm.gym.api.dtos.trainee.TraineeTokenWrapper;
import com.crm.gym.api.dtos.trainer.TrainerRegistrationRequest;
import com.crm.gym.api.dtos.trainer.TrainerTokenWrapper;
import com.crm.gym.api.dtos.training.TrainingScheduleRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TrainingControllerTest
{
    private static String TRAINEE_ACCESS_TOKEN;
    private static String TRAINER_ACCESS_TOKEN;

    @Autowired
    public TrainingControllerTest(TraineeController traineeController, TrainerController trainerController)
    {
        TraineeRegistrationRequest traineeRegistrationRequest = new TraineeRegistrationRequest();
        traineeRegistrationRequest.setFirstname("trainee");
        traineeRegistrationRequest.setLastname("test");

        TrainerRegistrationRequest trainerRegistrationRequest = new TrainerRegistrationRequest();
        trainerRegistrationRequest.setFirstname("trainer");
        trainerRegistrationRequest.setLastname("test");

        TRAINEE_ACCESS_TOKEN = ((TraineeTokenWrapper) traineeController.createTrainee(traineeRegistrationRequest).getContent()).getAccessToken();
        TRAINER_ACCESS_TOKEN = ((TrainerTokenWrapper) trainerController.createTrainer(trainerRegistrationRequest).getContent()).getAccessToken();
    }

    @BeforeAll
    static void beforeAll()
    {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    @DisplayName("Tests HTTP 200 & 400 on POST /trainings")
    void createTraining()
    {
        TrainingScheduleRequest trainingDto = new TrainingScheduleRequest();
        trainingDto.setName("Morning Fitness Blast");
        trainingDto.setTrainingType("Fitness");
        trainingDto.setDate(LocalDate.parse("2025-06-21"));
        trainingDto.setDuration(30);
        trainingDto.setTrainerUsername("John.Doe");
        trainingDto.setTraineeUsername("Alice.Smith");

        // 200 OK

        given()
            .header("Authorization", "Bearer " + TRAINEE_ACCESS_TOKEN)
            .accept(ContentType.JSON)
            .body(trainingDto)
            .contentType(ContentType.JSON)
        .when()
            .post("/trainings")
        .then()
            .statusCode(201);

        // 400 BAD_REQUEST

        trainingDto.setTraineeUsername(null);

        given()
            .header("Authorization", "Bearer " + TRAINEE_ACCESS_TOKEN)
            .accept(ContentType.JSON)
            .body(trainingDto)
            .contentType(ContentType.JSON)
        .when()
            .post("/trainings")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Tests HTTP 200 on GET /trainings")
    void getAllTrainings()
    {
        given()
            .header("Authorization", "Bearer " + TRAINEE_ACCESS_TOKEN)
            .accept(ContentType.JSON)
        .when()
            .get("/trainings")
        .then()
            .statusCode(200);
    }

    @Test
    @DisplayName("Tests HTTP 200 on GET /trainees/{traineeUsername}/trainings")
    void getTrainingsByTraineeUsernameAndCriteria()
    {
        String trainerUsername = "John.Doe";
        String traineeUsername = "Alice.Smith";
        String trainingTypeName = "Fitness";

        given()
            .header("Authorization", "Bearer " + TRAINEE_ACCESS_TOKEN)
            .accept(ContentType.JSON)
            .queryParam("trainerUsername", trainerUsername)
            .queryParam("fromDate", "2025-01-01")
            .queryParam("toDate", "2025-12-31")
            .queryParam("trainingTypeName", trainingTypeName)
        .when()
            .get("/trainees/{traineeUsername}/trainings", traineeUsername)
        .then()
            .statusCode(200)
            .rootPath("_embedded.trainings")
            .body("trainerUsername", everyItem(equalTo(trainerUsername)))
            .body("traineeUsername", everyItem(equalTo(traineeUsername)))
            .body("trainingType", everyItem(equalTo(trainingTypeName)));
    }

    @Test
    @DisplayName("Tests HTTP 200 on GET /trainers/{trainerUsername}/trainings")
    void getTrainingsByTrainerUsernameAndCriteria()
    {
        String traineeUsername = "Alice.Smith";
        String trainerUsername = "John.Doe";
        String trainingTypeName = "Fitness";

        given()
            .header("Authorization", "Bearer " + TRAINER_ACCESS_TOKEN)
            .accept(ContentType.JSON)
            .queryParam("traineeUsername", traineeUsername)
            .queryParam("fromDate", "2025-01-01")
            .queryParam("toDate", "2025-12-31")
            .queryParam("trainingTypeName", trainingTypeName)
        .when()
            .get("/trainers/{trainerUsername}/trainings", trainerUsername)
        .then()
            .statusCode(200)
            .rootPath("_embedded.trainings")
            .body("trainerUsername", everyItem(equalTo(trainerUsername)))
            .body("traineeUsername", everyItem(equalTo(traineeUsername)))
            .body("trainingType", everyItem(equalTo(trainingTypeName)));
    }
}