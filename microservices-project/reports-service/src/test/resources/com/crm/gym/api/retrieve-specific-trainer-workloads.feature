@SmokeTest
Feature: Get Trainer Workloads by Username

  @PositiveTest
  Scenario: Retrieve workloads for an existing trainer
    Given a trainer named "John.Doe" exists
    When a request is made to retrieve workloads for "John.Doe"
    Then the response should indicate that the trainer was found

  @NegativeTest
  Scenario: Return not found when the trainer does not exist
    Given no trainer named "John.Doe" exists
    When a request is made to retrieve workloads for "John.Doe"
    Then the response should indicate that the trainer was not found