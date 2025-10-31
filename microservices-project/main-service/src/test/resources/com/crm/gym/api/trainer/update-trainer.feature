@SmokeTest
Feature: Update Trainer Users

  Background:
    Given the following trainers exist:
      | Username    | Firstname | Lastname | Specialization | Status |
      | John.Doe    | John      | Doe      | Fitness        | true   |
      | Alice.Smith | Alice     | Smith    | Fitness        | true   |

  @PositiveTest
  Scenario: Update existing trainer user
    When the following update trainer request is made:
      | Username | Firstname | Lastname | Specialization | Status |
      | John.Doe | Jack      | Doherty  | Yoga           | false  |
    Then the response should be OK

  @NegativeTest
  Scenario: Update non-existing trainer user
    When the following update trainer request is made:
      | Username | Firstname | Lastname | Specialization | Status |
      | Jack.Doe | Doherty   | Doherty  | Yoga           | false  |
    Then the response should be Not Found

  @ErrorHandlingTest
  Scenario Outline: Reject trainer update requests with missing information
    When the following update trainer request is made:
      | Username   | Firstname   | Lastname   | Specialization   | Status   |
      | <Username> | <Firstname> | <Lastname> | <Specialization> | <Status> |
    Then the response should be Bad Request
    Examples:
      | Username | Firstname | Lastname | Specialization | Status |
      | John.Doe | N/A       | Doherty  | Yoga           | false  |
      | John.Doe | Jack      | N/A      | Yoga           | false  |
      | John.Doe | Jack      | Doherty  | N/A            | false  |
      | John.Doe | Jack      | Doherty  | Yoga           | N/A    |