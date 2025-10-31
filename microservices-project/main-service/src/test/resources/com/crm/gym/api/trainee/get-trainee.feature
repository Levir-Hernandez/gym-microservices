@SmokeTest
Feature: Get Trainee Users

  @PositiveTest
  Scenario: Retrieve all trainees when trainees exist
    Given multiple trainee users exist
    When a request is made to retrieve all trainees
    Then the response should be OK
    And the response should include a list of all trainees

  @NegativeTest
  Scenario: Retrieve all trainees when no trainees exist
    Given no trainee users exist
    When a request is made to retrieve all trainees
    Then the response should be OK
    And the response should include an empty list of trainees

  @PositiveTest
  Scenario: Retrieve an existing trainee by username
    Given a trainee with username "John.Doe" exists
    When a request is made to retrieve the trainee by username "John.Doe"
    Then the response should be OK

  @NegativeTest
  Scenario: Return not found when the trainee does not exist
    Given no trainee with username "John.Doe" exists
    When a request is made to retrieve the trainee by username "John.Doe"
    Then the response should be Not Found