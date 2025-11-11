@SmokeTest
Feature: Trainee registration

  @PositiveTest
  Scenario: Register a new trainee user
    Given a trainee with first name "John" and last name "Doe"
    When a request is made to create a trainee user
    Then the response should be Created
    And the generated trainee username should be "John.Doe"

  @NegativeTest
  Scenario Outline: Reject trainee registration with missing information
    Given a trainee with first name "<FirstName>" and last name "<LastName>"
    When a request is made to create a trainee user
    Then the response should be Bad Request
    Examples:
      | FirstName | LastName |
      | N/A       | Doe      |
      | John      | N/A      |