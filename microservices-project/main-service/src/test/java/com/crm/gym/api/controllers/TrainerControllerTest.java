package com.crm.gym.api.controllers;

import com.crm.gym.api.dtos.trainee.TraineeRegistrationRequest;
import com.crm.gym.api.dtos.trainee.TraineeTokenWrapper;
import com.crm.gym.api.dtos.trainer.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TrainerControllerTest
{
    private static String TRAINER_ACCESS_TOKEN;
    private static String TRAINEE_ACCESS_TOKEN;

    @Autowired
    public TrainerControllerTest(TrainerController trainerController, TraineeController traineeController)
    {
        TrainerRegistrationRequest trainerRegistrationRequest = new TrainerRegistrationRequest();
        trainerRegistrationRequest.setFirstname("trainer");
        trainerRegistrationRequest.setLastname("test");

        TrainerTokenWrapper trainerRespDto = (TrainerTokenWrapper) trainerController.createTrainer(trainerRegistrationRequest).getContent();

        TRAINER_ACCESS_TOKEN = trainerRespDto.getAccessToken();

        TrainerChangePasswordRequest trainerChangePasswordRequest = new TrainerChangePasswordRequest();
        trainerChangePasswordRequest.setUsername(trainerRespDto.getUsername());
        trainerChangePasswordRequest.setOldPassword(((TrainerCredentials)trainerRespDto.getUser()).getPassword());
        trainerChangePasswordRequest.setNewPassword("1234");

        trainerController.changePassword(trainerChangePasswordRequest);
        trainerController.deactivateTrainer(trainerRespDto.getUsername());

        TraineeRegistrationRequest traineeRegistrationRequest = new TraineeRegistrationRequest();
        traineeRegistrationRequest.setFirstname("trainee");
        traineeRegistrationRequest.setLastname("test");

        TRAINEE_ACCESS_TOKEN = ((TraineeTokenWrapper)traineeController.createTrainee(traineeRegistrationRequest).getContent()).getAccessToken();
    }
    
    @BeforeAll
    static void beforeAll()
    {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    @DisplayName("Tests HTTP 200 & 400 on POST /trainers")
    void createTrainer()
    {
        String traineeFirstname = "Larry";
        String traineeLastname = "Williams";

        TrainerRegistrationRequest trainerDto = new TrainerRegistrationRequest();
        trainerDto.setFirstname(traineeFirstname);
        trainerDto.setLastname(traineeLastname);
        trainerDto.setSpecialization("Yoga");

        // 200 OK

        given()
            .accept(ContentType.JSON)
            .body(trainerDto)
            .contentType(ContentType.JSON)
        .when()
            .post("/trainers")
        .then()
            .statusCode(201)
            .rootPath("trainer")
            .body("username", equalTo(traineeFirstname+"."+traineeLastname));

        // 400 BAD_REQUEST

        trainerDto.setFirstname(null);

        given()
            .accept(ContentType.JSON)
            .body(trainerDto)
            .contentType(ContentType.JSON)
        .when()
            .post("/trainers")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Tests HTTP 200 on GET /trainers")
    void getAllTrainers()
    {
        given()
            .header("Authorization", "Bearer " + TRAINER_ACCESS_TOKEN)
            .accept(ContentType.JSON)
        .when()
            .get("/trainers")
        .then()
            .statusCode(200);
    }

    @Test
    @DisplayName("Tests HTTP 200 & 404 on GET /trainers/{username}")
    void getTrainerByUsername()
    {
        // 200 OK

        String firstname = "Tom";
        String lastname = "Anderson";
        String username = firstname+"."+lastname;

        given()
            .header("Authorization", "Bearer " + TRAINER_ACCESS_TOKEN)
            .accept(ContentType.JSON)
        .when()
            .get("/trainers/{username}", username)
        .then()
            .statusCode(200)
            .body("firstname", equalTo(firstname))
            .body("lastname", equalTo(lastname));

        // 400 NOT_FOUND

        username = "Unknown.Unknown";

        given()
            .header("Authorization", "Bearer " + TRAINER_ACCESS_TOKEN)
            .accept(ContentType.JSON)
        .when()
            .get("/trainers/{username}", username)
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("Tests HTTP 200 & 400 on PUT /trainers/{username}")
    void updateTrainerByUsername()
    {
        String username = "Laura.Williams";
        String newTraineeFirstname = "Mary";
        String newTraineeLastname = "Rosebud";
        TrainerModificationRequest trainerDto = new TrainerModificationRequest();
        trainerDto.setFirstname(newTraineeFirstname);
        trainerDto.setLastname(newTraineeLastname);
        trainerDto.setSpecialization("Zumba");
        trainerDto.setIsActive(true);

        // 200 OK

        given()
            .header("Authorization", "Bearer " + TRAINER_ACCESS_TOKEN)
            .accept(ContentType.JSON)
            .body(trainerDto)
            .contentType(ContentType.JSON)
        .when()
            .put("/trainers/{username}", username)
        .then()
            .statusCode(200)
            .body("firstname", equalTo(newTraineeFirstname))
            .body("lastname", equalTo(newTraineeLastname));

        // 400 BAD_REQUEST

        trainerDto.setFirstname(null);

        given()
            .header("Authorization", "Bearer " + TRAINER_ACCESS_TOKEN)
            .accept(ContentType.JSON)
            .body(trainerDto)
            .contentType(ContentType.JSON)
        .when()
            .put("/trainers/{username}", username)
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Tests HTTP 200 & 404 on PATCH /trainers/{username}/activate")
    void activateTrainer()
    {
        // 200 OK

        String username = "Laura.Williams";
        given()
            .header("Authorization", "Bearer " + TRAINER_ACCESS_TOKEN)
            .accept(ContentType.JSON)
        .when()
            .patch("/trainers/{username}/activate", username)
        .then()
            .statusCode(200);

        // 404 NOT_FOUND

        username = "Unknown.Unknown";
        given()
            .header("Authorization", "Bearer " + TRAINER_ACCESS_TOKEN)
            .accept(ContentType.JSON)
        .when()
            .patch("/trainers/{username}/activate", username)
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("Tests HTTP 200 & 404 on PATCH /trainers/{username}/deactivate")
    void deactivateTrainer()
    {
        // 200 OK

        String username = "Tom.Anderson";
        given()
            .header("Authorization", "Bearer " + TRAINER_ACCESS_TOKEN)
            .accept(ContentType.JSON)
        .when()
            .patch("/trainers/{username}/deactivate", username)
        .then()
            .statusCode(200);

        // 404 NOT_FOUND

        username = "Unknown.Unknown";
        given()
            .header("Authorization", "Bearer " + TRAINER_ACCESS_TOKEN)
            .accept(ContentType.JSON)
        .when()
            .patch("/trainers/{username}/activate", username)
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("Tests HTTP 200 & 401 on POST trainers/login")
    void login()
    {
        TrainerLoginRequest trainerDto = new TrainerLoginRequest();

        // 200 OK

        trainerDto.setUsername("trainer.test");
        trainerDto.setPassword("1234");

        given()
            .accept(ContentType.JSON)
            .body(trainerDto)
            .contentType(ContentType.JSON)
        .when()
            .post("/trainers/login")
        .then()
            .statusCode(200);

        // 401 UNAUTHORIZED

        trainerDto.setUsername("invalid.invalid");
        trainerDto.setPassword("invalid");

        given()
            .accept(ContentType.JSON)
            .body(trainerDto)
            .contentType(ContentType.JSON)
        .when()
            .post("/trainers/login")
        .then()
            .statusCode(401);
    }

    @Test
    @DisplayName("Tests HTTP 200 & 401 on PUT trainers/change-password")
    void changePassword()
    {
        TrainerChangePasswordRequest trainerDto = new TrainerChangePasswordRequest();

        // 200 OK

        trainerDto.setUsername("trainer.test");
        trainerDto.setOldPassword("1234");
        trainerDto.setNewPassword("1234");

        given()
            .accept(ContentType.JSON)
            .body(trainerDto)
            .contentType(ContentType.JSON)
        .when()
            .put("/trainers/change-password")
        .then()
            .statusCode(200);

        // 401 UNAUTHORIZED

        trainerDto.setUsername("invalid.invalid");

        given()
            .accept(ContentType.JSON)
            .body(trainerDto)
            .contentType(ContentType.JSON)
        .when()
            .put("/trainers/change-password")
        .then()
            .statusCode(401);
    }

    @Test
    @DisplayName("Tests HTTP 200 & 404 on GET /trainees/{traineeUsername}/trainers/unassigned")
    void getAllUnassignedForTraineeByUsername()
    {
        // 200 OK

        String traineeUsername = "Alice.Smith";
        given()
            .header("Authorization", "Bearer " + TRAINEE_ACCESS_TOKEN)
            .accept(ContentType.JSON)
        .when()
            .get("/trainees/{traineeUsername}/trainers/unassigned", traineeUsername)
        .then()
            .statusCode(200)
            .rootPath("_embedded.trainers")
            .body("username", containsInAnyOrder("Mike.Johnson", "Laura.Williams", "Larry.Williams"));

        // 404 NOT_FOUND

        traineeUsername = "Unknown.Unknown";
        given()
            .header("Authorization", "Bearer " + TRAINEE_ACCESS_TOKEN)
            .accept(ContentType.JSON)
        .when()
            .get("/trainees/{traineeUsername}/trainers/unassigned", traineeUsername)
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("Tests HTTP 200 & 400 & 404 on PUT /trainees/{traineeUsername}/trainers/assigned")
    void updateAssignedTrainersForTrainee()
    {
        String traineeUsername = "Alice.Smith";

        TrainerModificationEmbeddedRequest trainerDto = new TrainerModificationEmbeddedRequest();
        trainerDto.setUsername("Jane.Smith");
        trainerDto.setFirstname("Jennifer");
        trainerDto.setLastname("Brown");
        trainerDto.setSpecialization("Fitness");
        trainerDto.setIsActive(false);

        Set<TrainerModificationEmbeddedRequest> trainerDtos = Set.of(trainerDto);

        given()
            .header("Authorization", "Bearer " + TRAINEE_ACCESS_TOKEN)
            .accept(ContentType.JSON)
            .body(trainerDtos)
            .contentType(ContentType.JSON)
        .when()
            .put("/trainees/{traineeUsername}/trainers/assigned", traineeUsername)
        .then()
            .statusCode(200);

        // 400 BAD_REQUEST

        trainerDto.setUsername(null);

        given()
            .header("Authorization", "Bearer " + TRAINEE_ACCESS_TOKEN)
            .accept(ContentType.JSON)
            .body(trainerDtos)
            .contentType(ContentType.JSON)
        .when()
            .put("/trainees/{traineeUsername}/trainers/assigned", traineeUsername)
        .then()
            .statusCode(400);

        // 404 NOT_FOUND

        traineeUsername = "Unknown.Unknown";
        trainerDto.setUsername("Jane.Smith");

        given()
            .header("Authorization", "Bearer " + TRAINEE_ACCESS_TOKEN)
            .accept(ContentType.JSON)
            .body(trainerDtos)
            .contentType(ContentType.JSON)
        .when()
            .put("/trainees/{traineeUsername}/trainers/assigned", traineeUsername)
        .then()
            .statusCode(404);
    }
}