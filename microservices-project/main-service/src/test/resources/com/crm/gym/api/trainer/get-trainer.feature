@SmokeTest
Feature: Get Trainer Users

  @PositiveTest
  Scenario: Retrieve all trainers when trainers exist
    Given multiple trainer users exist
    When a request is made to retrieve all trainers
    Then the response should be OK
    And the response should include a list of all trainers

  @NegativeTest
  Scenario: Retrieve all trainers when no trainers exist
    Given no trainer users exist
    When a request is made to retrieve all trainers
    Then the response should be OK
    And the response should include an empty list of trainers

  @PositiveTest
  Scenario: Retrieve an existing trainer by username
    Given a trainer with username "John.Doe" exists
    When a request is made to retrieve the trainer by username "John.Doe"
    Then the response should be OK

  @NegativeTest
  Scenario: Return not found when the trainer does not exist
    Given no trainer with username "John.Doe" exists
    When a request is made to retrieve the trainer by username "John.Doe"
    Then the response should be Not Found