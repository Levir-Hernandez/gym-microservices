@SmokeTest
Feature: Trainer registration

  @PositiveTest
  Scenario: Register a new trainer user
    Given a trainer with first name "John", last name "Doe" and "Fitness" specialization
    When a request is made to create a trainer user
    Then the response should be Created
    And the generated trainer username should be "John.Doe"

  @NegativeTest
  Scenario Outline: Reject trainer registration with missing information
    Given a trainer with first name "<FirstName>", last name "<LastName>" and "<Specialization>" specialization
    When a request is made to create a trainer user
    Then the response should be Bad Request
    Examples:
      | FirstName | LastName | Specialization |
      | N/A       | Doe      | Fitness        |
      | John      | N/A      | Fitness        |
      | John      | Doe      | N/A            |