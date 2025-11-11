@SmokeTest
Feature: Update Trainee Users

  Background:
    Given the following trainees exist:
      | Username    | Firstname | Lastname | Status |
      | John.Doe    | John      | Doe      | true   |
      | Alice.Smith | Alice     | Smith    | true   |

  @PositiveTest
  Scenario: Update existing trainee user
    When the following update trainee request is made:
      | Username | Firstname | Lastname | Status |
      | John.Doe | Jack      | Doherty  | false  |
    Then the response should be OK

  @NegativeTest
  Scenario: Update non-existing trainee user
    When the following update trainee request is made:
      | Username     | Firstname | Lastname | Status |
      | Jack.Doherty | Jack      | Doherty  | false  |
    Then the response should be Not Found

  @ErrorHandlingTest
  Scenario Outline: Reject trainee update requests with missing information
    When the following update trainee request is made:
      | Username   | Firstname   | Lastname   | Status   |
      | <Username> | <Firstname> | <Lastname> | <Status> |
    Then the response should be Bad Request
    Examples:
      | Username | Firstname | Lastname | Status |
      | John.Doe | N/A       | Doherty  | false  |
      | John.Doe | Jack      | N/A      | false  |
      | John.Doe | Jack      | Doherty  | N/A    |