@IntegrationTest
Feature: End-to-end Training Creation Flow

  @PositiveTest
  Scenario: Register a trainer, create a training, and verify the workload report
    Given the main service is up and running
    When a trainer registration request is submitted with the following details:
      | FirstName | LastName | Specialization |
      | John      | Doe      | Fitness        |
    Then the response status should be Created
    And the generated username should be "John.Doe"
    And the response should contain an access token

    When the trainer "John.Doe" creates a training session with the following details:
      | Training name         | Training type | Training date | Training duration | Trainee username |
      | Morning Fitness Blast | Fitness       | 2025-06-21    | 30                | Alice.Smith      |
    Then the response status should be Created

    Given the reports service is up and running
    When a workload report is requested for trainer "John.Doe"
    Then the response status should be OK
    And the workload summary for 2025-06 should show a total duration of 30 minutes

  @NegativeTest
  Scenario: Reject training creation with expired session
    Given the main service is up and running
    And the trainer "John.Doe" session has expired
    When the trainer "John.Doe" creates a training session with the following details:
      | Training name         | Training type | Training date | Training duration | Trainee username |
      | Morning Fitness Blast | Fitness       | 2025-06-21    | 30                | Alice.Smith      |
    Then the response status should be Forbidden