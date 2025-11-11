@SmokeTest
Feature: Get All Trainer Workloads

  @PositiveTest
  Scenario: Retrieve all workloads when trainers exist
    Given multiple trainers exist
    When a request is made to retrieve all trainer workloads
    Then the response should include a list of all trainer workloads

  @NegativeTest
  Scenario: Retrieve an empty list when no trainers exist
    Given no trainers exist
    When a request is made to retrieve all trainer workloads
    Then the response should include an empty list