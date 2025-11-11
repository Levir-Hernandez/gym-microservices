@SmokeTest
Feature: Change Trainer State

  @PositiveTest
  Scenario: Activate an inactive trainer user
    Given an deactivated trainer named "John.Doe"
    When a request is made to activate trainer "John.Doe"
    Then the response should be OK

  @NegativeTest
  Scenario: Reject activation of a non-existent trainer user
    Given no trainer named "John.Doe"
    When a request is made to activate trainer "John.Doe"
    Then the response should be Not Found

  @PositiveTest
  Scenario: Deactivate an active trainer user
    Given an activated trainer named "John.Doe"
    When a request is made to deactivate trainer "John.Doe"
    Then the response should be OK

  @NegativeTest
  Scenario: Reject deactivation of a non-existent trainer user
    Given no trainer named "John.Doe"
    When a request is made to deactivate trainer "John.Doe"
    Then the response should be Not Found