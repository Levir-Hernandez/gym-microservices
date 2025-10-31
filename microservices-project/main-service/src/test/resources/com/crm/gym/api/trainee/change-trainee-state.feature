@SmokeTest
Feature: Change Trainee State

  @PositiveTest
  Scenario: Activate an inactive trainee user
    Given an deactivated trainee named "John.Doe"
    When a request is made to activate trainee "John.Doe"
    Then the response should be OK

  @NegativeTest
  Scenario: Reject activation of a non-existent trainee user
    Given no trainee named "John.Doe"
    When a request is made to activate trainee "John.Doe"
    Then the response should be Not Found

  @PositiveTest
  Scenario: Deactivate an active trainee user
    Given an activated trainee named "John.Doe"
    When a request is made to deactivate trainee "John.Doe"
    Then the response should be OK

  @NegativeTest
  Scenario: Reject deactivation of a non-existent trainee user
    Given no trainee named "John.Doe"
    When a request is made to deactivate trainee "John.Doe"
    Then the response should be Not Found