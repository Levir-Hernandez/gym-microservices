@SecurityTest
Feature: Permission Control for Workload Operations

  @PositiveTest
  Scenario: Allow access when the user has the Trainer role
    Given any protected workload operation
    And the user is authenticated as "TRAINER"
    When the user sends a request
    Then the response should be OK

  @ErrorHandlingTest
  Scenario: Forbid access when the user has the Trainee role
    Given any protected workload operation
    And the user is authenticated as "TRAINEE"
    When the user sends a request
    Then the response should be Forbidden

  @ErrorHandlingTest
  Scenario: Deny access when the user is not authenticated
    Given any protected workload operation
    And the user is not authenticated
    When the user sends a request
    Then the response should be Unauthorized