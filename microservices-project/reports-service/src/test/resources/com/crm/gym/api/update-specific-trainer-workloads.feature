@SmokeTest
Feature: Update Trainer Workloads

  Background:
    Given a trainer named "John.Doe" exists with the following workload summary:
      | Year | Month | Trainer Workload |
      | 2024 | 11    | 220              |
      | 2025 | 3     | 90               |

  @PositiveTest
  Scenario: Increase the workload of an existing trainer
    Given the following workload update request:
      | Trainer username | Training date | Training duration |
      | John.Doe         | 2025-03-07    | 60                |
    When it is made to "ADD" workload for the trainer
    Then the final workload for 2025-03 should be 150 hours
    And the final workload for 2024-11 should be 220 hours

  @NegativeTest
  Scenario: Create a new workload record for a non-existing trainer
    Given the following workload update request:
      | Trainer username | Training date | Training duration |
      | Jane.Smith       | 2025-03-07    | 60                |
    When it is made to "ADD" workload for the trainer
    Then a new workload summary should be created for "Jane.Smith"
    And the final workload for 2025-03 should be 60 hours

  @PositiveTest
  Scenario: Reduce the workload of an existing trainer
    Given the following workload update request:
      | Trainer username | Training date | Training duration |
      | John.Doe         | 2025-03-07    | 60                |
    When it is made to "DELETE" workload for the trainer
    Then the final workload for 2025-03 should be 30 hours
    And the final workload for 2024-11 should be 220 hours

  @NegativeTest
  Scenario: Reduce the trainer workload beyond the existing amount
    Given the following workload update request:
      | Trainer username | Training date | Training duration |
      | John.Doe         | 2025-03-07    | 120               |
    When it is made to "DELETE" workload for the trainer
    Then the final workload for 2025-03 should be 0 hours
    And the final workload for 2024-11 should be 220 hours

  @ErrorHandlingTest
  Scenario Outline: Reject workload update requests with missing information
    Given the following workload update request:
      | Trainer username   | Training date   | Training duration   |
      | <Trainer username> | <Training date> | <Training duration> |
    When it is made to "<Action>" workload for the trainer
    Then the response should be Bad Request
    Examples:
      | Action | Trainer username | Training date | Training duration |
      | N/A    | John.Doe         | 2025-03-07    | 60                |
      | ADD    | N/A              | 2025-03-07    | 60                |
      | ADD    | John.Doe         | N/A           | 60                |
      | ADD    | John.Doe         | 2025-03-07    | N/A               |