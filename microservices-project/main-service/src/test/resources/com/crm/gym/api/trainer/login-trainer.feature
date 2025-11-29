@SmokeTest
Feature: Login Trainer User

  Background:
    Given the following trainers' credentials:
      | Username   | Password   |
      | John.Smith | 0123456789 |
      | Alice.Doe  | 9876543210 |
    And none of the trainers are locked out

  @PositiveTest
  Scenario: Trainer log in with valid credentials
    When a request is made to log in with trainer username "John.Smith" and password "0123456789"
    Then the response should be OK

  @NegativeTest
  Scenario: Trainer log in with invalid credentials
    When a request is made to log in with trainer username "Jack.Smith" and password "0123456789"
    Then the response should be Unauthorized

  @PositiveTest
  Scenario: Trainer change password with valid credentials
    When the following trainer change password request is made:
      | Username   | Old password | New password |
      | John.Smith | 0123456789   | abcdefghij   |
    Then the response should be OK

  @NegativeTest
  Scenario: Trainer change password with invalid credentials
    When the following trainer change password request is made:
      | Username   | Old password | New password |
      | Jack.Smith | 0123456789   | abcdefghij   |
    Then the response should be Unauthorized

  @SecurityTest
  Scenario Outline: Trainer brute-force protection after three failed logins
    When a request is made to log in with trainer username "<Username>" and password "<Password>"
    Then the response should be <Response>
    Examples:
      | Username   | Password   | Response     |
      #-----------Before protection----------#
      | John.Smith | 0123456789 | OK           |
      #----------Trigger protection----------#
      | John.Smith | 0000000000 | Unauthorized |
      | John.Smith | 1111111111 | Unauthorized |
      | John.Smith | 2222222222 | Unauthorized |
      #-----------After protection-----------#
      | John.Smith | 0123456789 | Forbidden    |